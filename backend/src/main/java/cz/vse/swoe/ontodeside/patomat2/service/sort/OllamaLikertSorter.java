package cz.vse.swoe.ontodeside.patomat2.service.sort;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.swoe.ontodeside.patomat2.config.ApplicationConfig;
import cz.vse.swoe.ontodeside.patomat2.exception.LlmSortException;
import cz.vse.swoe.ontodeside.patomat2.exception.MaxSortableInstancesThresholdExceededException;
import cz.vse.swoe.ontodeside.patomat2.model.PatternInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Sorts patterns instances by their likelihood of "making sense" using a Likert scale.
 * <p>
 * Uses an external LLM accessed via Ollama.
 */
@Component
public class OllamaLikertSorter implements PatternInstanceSorter {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaLikertSorter.class);

    private static final String promptTemplateFile = "llm_sort_system_prompt.txt";

    /**
     * JSON schema for the output of the LLM.
     */
    private static final String OUTPUT_FORMAT = """
            {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "input_number": { "type": "integer" },
                      "likert_score": { "type": "integer", "minimum": 1, "maximum": 5 },
                      "suggested_label": { "type": "string" }
                    },
                    "required": ["input_number", "likert_score", "suggested_label"]
                  }
                }
            """;

    private final ApplicationConfig.LLM.Sort sortConfig;

    private final RestTemplate restTemplate;

    public OllamaLikertSorter(ApplicationConfig config, RestTemplate restTemplate) {
        this.sortConfig = config.getLlm().getSort();
        this.restTemplate = restTemplate;
    }

    @Override
    public List<PatternInstance> sort(List<PatternInstance> patternInstances) {
        Objects.requireNonNull(patternInstances);
        if (patternInstances.size() > sortConfig.getMaxInstances()) {
            throw new MaxSortableInstancesThresholdExceededException("Cannot sort, maximum sortable number of pattern instances (" + sortConfig.getMaxInstances() + ") exceeded.");
        }
        if (patternInstances.size() > sortConfig.getBatchSize()) {
            LOG.trace("Sorting sublist of patterns.");
            patternInstances = new ArrayList<>(patternInstances.subList(0, sortConfig.getBatchSize()));
        }
        String promptTemplate = loadPromptTemplate();
        assert !promptTemplate.isBlank();

        promptTemplate = promptTemplate.replace("$SPARQL$", patternInstances.getFirst().pattern()
                                                                            .createInsertSparqlTemplate());
        final String values = "These are the values for your task:\n\n" + constructValues(patternInstances);
        final OllamaInput input = new OllamaInput(sortConfig.getModel(), promptTemplate, values, OUTPUT_FORMAT, Map.of("num_ctx", 8092), false);
        LOG.debug("Sorting pattern matches using Ollama.");
        final long start = System.currentTimeMillis();
        try {
            final ResponseEntity<OllamaOutput> response = restTemplate.postForEntity(sortConfig.getApiUrl() + "/api/generate", input, OllamaOutput.class);
            assert response.getBody() != null;
            final List<ResultRow> result = new ObjectMapper().readValue(response.getBody()
                                                                                .response(), new TypeReference<>() {});
            final long end = System.currentTimeMillis();
            LOG.trace("Received Ollama response: {}.", result);
            LOG.debug("Ollama invocation took {} s.", (end - start) / 1000);
            assert result != null;
            return actuallySort(patternInstances, result);
        } catch (Exception e) {
            LOG.error("Unable to retrieve LLM response or process it.", e);
            throw new LlmSortException("Unable to retrieve LLM response or process it.");
        }
    }

    private static String loadPromptTemplate() {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                OllamaLikertSorter.class.getClassLoader().getResourceAsStream(promptTemplateFile)))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            LOG.error("Unable to load prompt template.", e);
            return "";
        }
    }

    private String constructValues(List<PatternInstance> patternInstances) {
        final StringBuilder sb = new StringBuilder();
        for (PatternInstance pi : patternInstances) {
            pi.match().getBindings()
              .forEach(rb -> sb.append('?').append(rb.name()).append('=').append(rb.label().orElse(rb.value()))
                               .append("\n"));
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    private List<PatternInstance> actuallySort(List<PatternInstance> patternInstances, List<ResultRow> sortRows) {
        final List<PatternInstance> result = new ArrayList<>(patternInstances.size());
        if (sortRows.size() != patternInstances.size()) {
            throw new LlmSortException("LLM response does not contain equal number of items as provided pattern instances.");
        }
        sortRows.sort(Comparator.comparing(ResultRow::likertScore).reversed());
        for (ResultRow row : sortRows) {
            if (row.number() > patternInstances.size()) {
                LOG.error("LLM returned line number {} (1-based) which is greater than the number of pattern instances.", row.number());
                continue;
            }
            result.add(patternInstances.get(row.number() - 1));
        }
        return result;
    }

    @Override
    public Sort getSortMethod() {
        return Sort.LLM_LIKERT;
    }

    private record OllamaInput(String model, String system, String prompt, @JsonRawValue String format,
                               Map<String, Object> options,
                               boolean stream) {}

    private record OllamaOutput(String response, String model) {}

    private record ResultRow(@JsonProperty("input_number") int number, @JsonProperty("likert_score") int likertScore,
                             @JsonProperty("suggested_label") String label) {}
}
