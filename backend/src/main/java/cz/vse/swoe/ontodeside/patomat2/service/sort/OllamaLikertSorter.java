package cz.vse.swoe.ontodeside.patomat2.service.sort;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.config.ApplicationConfig;
import cz.vse.swoe.ontodeside.patomat2.exception.LlmSortException;
import cz.vse.swoe.ontodeside.patomat2.exception.MaxSortableInstancesThresholdExceededException;
import cz.vse.swoe.ontodeside.patomat2.model.EntityLabel;
import cz.vse.swoe.ontodeside.patomat2.model.ExampleValues;
import cz.vse.swoe.ontodeside.patomat2.model.Pattern;
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
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Sorts patterns instances by their likelihood of "making sense" using a Likert scale.
 * <p>
 * Uses an external LLM accessed via Ollama.
 */
@Component
public class OllamaLikertSorter implements PatternInstanceSorter {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaLikertSorter.class);

    private static final String promptTemplateFile = "llm_sort_system_prompt.txt";
    private static final String promptTemplateFileMulti = "llm_sort_system_prompt_multi.txt";

    /**
     * JSON schema for the output of the LLM when there are multiple new entities.
     * <p>
     * A separate schema is defined for cases with multiple and single variable because the LLM was returning an empty
     * array of suggested_label items when there was only one new entity variable.
     */
    private static final String OUTPUT_FORMAT_MULTI = """
            {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "input_number": { "type": "integer" },
                      "likert_score": { "type": "integer", "minimum": 1, "maximum": 5 },
                      "suggested_label": { "type":
                            "array",
                            "items": {
                                "type": "object",
                                "properties": {"variable": {"type": "string"}, "label": {"type": "string"}}
                            }
                      }
                    },
                    "required": ["input_number", "likert_score", "suggested_label"]
                  }
                }
            """;

    /**
     * JSON schema for the output of the LLM when there is a single new entity.
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
        if (patternInstances.isEmpty()) {
            return patternInstances;
        }
        if (patternInstances.size() > sortConfig.getMaxInstances()) {
            throw new MaxSortableInstancesThresholdExceededException("Cannot sort, maximum sortable number of pattern instances (" + sortConfig.getMaxInstances() + ") exceeded.");
        }
        if (patternInstances.size() > sortConfig.getBatchSize()) {
            return sortInBatches(patternInstances);
        } else {
            return sortSimple(patternInstances);
        }
    }

    private List<PatternInstance> sortInBatches(List<PatternInstance> instances) {
        LOG.debug("Running parallel sort with multiple batches.");
        final long start = System.currentTimeMillis();

        final Pattern pattern = instances.getFirst().pattern();
        final String systemPrompt = loadAndPopulateSystemPromptTemplate(pattern);
        assert !systemPrompt.isBlank();
        final int batchSize = sortConfig.getBatchSize();
        final int roundSize = batchSize * sortConfig.getMaxConcurrentRequests();
        final int roundCount = (int) Math.ceil((double) instances.size() / roundSize);
        final ResultRow[] sortRows = new ResultRow[instances.size()];
        for (int i = 0; i < roundCount; i++) {
            LOG.trace("Running batch sort, round {}", (i + 1));
            final CountDownLatch endLatch = new CountDownLatch(sortConfig.getMaxConcurrentRequests());
            final int round = i * roundSize;
            for (int j = 0; j < sortConfig.getMaxConcurrentRequests(); j++) {
                var position = round + j * batchSize;
                if (position >= instances.size()) {
                    endLatch.countDown();
                    continue;
                }
                final List<PatternInstance> batch = instances.subList(position, Math.min((round + ((j + 1) * batchSize)), instances.size()));
                runConcurrentSortOfBatch(systemPrompt, batch, sortRows, position, endLatch);
            }
            try {
                endLatch.await();
            } catch (InterruptedException e) {
                LOG.error("Batch sorting interrupted.", e);
                Thread.currentThread().interrupt();
                throw new LlmSortException("Batch sorting interrupted.");
            }
        }
        final List<PatternInstance> result = actuallySort(instances, Stream.of(sortRows).filter(Objects::nonNull)
                                                                           .collect(Collectors.toList()));
        final long end = System.currentTimeMillis();
        LOG.debug("Batch sorting took {} s.", (end - start) / 1000);
        return result;
    }

    private static String stringifyExamples(List<ExampleValues> exampleValues) {
        return exampleValues.stream()
                            .map(example -> example.bindings().stream()
                                                   .map(binding -> "?" + binding.name() + "=" + binding.value())
                                                   .collect(Collectors.joining("\n")))
                            .collect(Collectors.joining("\n\n and \n\n"));
    }

    private void runConcurrentSortOfBatch(String systemPrompt, List<PatternInstance> batch, ResultRow[] sortRows,
                                          int position, CountDownLatch endLatch) {
        new Thread(() -> {
            final List<ResultRow> llmResult = callForBatch(systemPrompt, batch);
            for (int r = 0; r < llmResult.size(); r++) {
                final ResultRow row = llmResult.get(r);
                sortRows[position + r] = row.withNumber(position + r + 1);
            }
            endLatch.countDown();
        }).start();
    }

    private List<PatternInstance> sortSimple(List<PatternInstance> instances) {
        LOG.debug("Running simple sort with one batch.");
        final String systemPrompt = loadAndPopulateSystemPromptTemplate(instances.getFirst().pattern());
        assert !systemPrompt.isBlank();

        final List<ResultRow> llmResult = callForBatch(systemPrompt, instances);
        return actuallySort(instances, llmResult);
    }

    private static String loadAndPopulateSystemPromptTemplate(Pattern pattern) {
        final String templateFile = pattern.newEntityVariables()
                                           .size() > 1 ? promptTemplateFileMulti : promptTemplateFile;
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                OllamaLikertSorter.class.getClassLoader().getResourceAsStream(templateFile)))) {
            final String template = reader.lines().collect(Collectors.joining("\n"));
            return template.replace("$SPARQL$", pattern.createInsertSparqlTemplate())
                           .replace("$EXAMPLE$", stringifyExamples(pattern.examples()))
                           .replace("$NEW_ENTITY$", String.join(", ", pattern.newEntityVariables()));
        } catch (IOException e) {
            LOG.error("Unable to load prompt template.", e);
            return "";
        }
    }

    private List<ResultRow> callForBatch(String systemPrompt, List<PatternInstance> instances) {
        final String values = "These are the values for your task:\n\n" + constructValues(instances);
        final boolean multipleNewEntities = instances.getFirst().pattern().newEntityVariables().size() > 1;
        final OllamaInput input = new OllamaInput(sortConfig.getModel(), systemPrompt, values, multipleNewEntities ? OUTPUT_FORMAT_MULTI : OUTPUT_FORMAT, Map.of("num_ctx", sortConfig.getNumCtx()), false);
        LOG.trace("Calling Ollama for batch of {} pattern instances.", instances.size());
        LOG.trace("LLM input: {}", input);
        final long start = System.currentTimeMillis();
        try {
            final ResponseEntity<OllamaOutput> response = restTemplate.postForEntity(sortConfig.getApiUrl() + "/api/generate", input, OllamaOutput.class);
            assert response.getBody() != null;
            final List<? extends ResultRow> result;
            if (multipleNewEntities) {
                result = new ObjectMapper().readValue(response.getBody()
                                                              .response(), new TypeReference<List<ResultRowMulti>>() {});
            } else {
                result = new ObjectMapper().readValue(response.getBody()
                                                              .response(), new TypeReference<List<ResultRowSingle>>() {});
            }
            final long end = System.currentTimeMillis();
            LOG.trace("Received Ollama response: {}.", result);
            LOG.trace("Ollama invocation took {} s.", (end - start) / 1000);
            return (List<ResultRow>) result;
        } catch (Exception e) {
            LOG.error("Unable to retrieve LLM response or process it.", e);
            throw new LlmSortException("Unable to retrieve LLM response or process it.");
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
        if (sortRows.size() > patternInstances.size()) {
            throw new LlmSortException("LLM response does not contain equal number of items as provided pattern instances. " +
                    "Expected " + patternInstances.size() + ", but got " + sortRows.size());
        }
        if (sortRows.size() < patternInstances.size()) {
            LOG.warn("LLM return fewer results than provided instances ({}). Remaining instances will be added to the end.", sortRows.size());
        }
        sortRows.sort(Comparator.comparing(ResultRow::likertScore).reversed());
        final boolean multipleNewEntities = patternInstances.getFirst().pattern().newEntityVariables().size() > 1;
        for (ResultRow row : sortRows) {
            if (row.number() > patternInstances.size()) {
                LOG.error("LLM returned line number {} (1-based) which is greater than the number of pattern instances. Ignoring it.", row.number());
                continue;
            }
            final PatternInstance toMove = patternInstances.get(row.number() - 1);
            // TODO Temporary workaround for providing LLM-suggested label of new entity
            if (multipleNewEntities) {
                addMultipleNewEntitiesLabels((ResultRowMulti) row, toMove);
            } else {
                addSingleNewEntityLabel((ResultRowSingle) row, toMove);
            }
            result.add(toMove.withLikertScore(row.likertScore()));
        }
        // Add remaining instances in case LLM returned fewer results
        for (int i = sortRows.size() + 1; i <= patternInstances.size(); i++) {
            result.add(patternInstances.get(i - 1));
        }
        LOG.trace("Sorting completed.");
        return result;
    }

    private static void addMultipleNewEntitiesLabels(ResultRowMulti row, PatternInstance toMove) {
        for (NewEntityLabel label : row.labels()) {
            toMove.newEntities().stream().filter(ne -> ne.variableName().equals(label.variable()))
                  .forEach(ne -> ne.labels()
                                   .add(new EntityLabel(makeLabelHumanReadable(label.label()), Constants.DEFAULT_LABEL_PROPERTY)));
        }
    }

    private static void addSingleNewEntityLabel(ResultRowSingle row, PatternInstance toMove) {
        final String variable = toMove.pattern().newEntityVariables().iterator().next();
        toMove.newEntities().stream().filter(ne -> ne.variableName().equals(variable))
              .forEach(ne -> ne.labels()
                               .add(new EntityLabel(makeLabelHumanReadable(row.label()), Constants.DEFAULT_LABEL_PROPERTY)));
    }

    private static String makeLabelHumanReadable(String label) {
        return label.replaceAll("[_-]", " ");
    }

    @Override
    public Sort getSortMethod() {
        return Sort.LLM_LIKERT;
    }


    // ------ Auxiliary DTO records -----------

    private record OllamaInput(String model, String system, String prompt, @JsonRawValue String format,
                               Map<String, Object> options,
                               boolean stream) {}

    private record OllamaOutput(String response, String model) {}

    private interface ResultRow {
        int number();

        int likertScore();

        ResultRow withNumber(int number);
    }

    private record ResultRowSingle(@JsonProperty("input_number") int number,
                                   @JsonProperty("likert_score") int likertScore,
                                   @JsonProperty("suggested_label") String label) implements ResultRow {

        public ResultRowSingle withNumber(int number) {
            return new ResultRowSingle(number, likertScore, label);
        }
    }

    private record ResultRowMulti(@JsonProperty("input_number") int number,
                                  @JsonProperty("likert_score") int likertScore,
                                  @JsonProperty("suggested_label") List<NewEntityLabel> labels) implements ResultRow {

        public ResultRowMulti withNumber(int number) {
            return new ResultRowMulti(number, likertScore, labels);
        }
    }

    private record NewEntityLabel(String variable, String label) {}
}
