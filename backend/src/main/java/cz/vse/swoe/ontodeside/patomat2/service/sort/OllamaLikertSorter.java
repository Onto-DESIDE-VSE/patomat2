package cz.vse.swoe.ontodeside.patomat2.service.sort;

import cz.vse.swoe.ontodeside.patomat2.config.ApplicationConfig;
import cz.vse.swoe.ontodeside.patomat2.exception.LlmSortException;
import cz.vse.swoe.ontodeside.patomat2.exception.MaxSortableInstancesThresholdExceededException;
import cz.vse.swoe.ontodeside.patomat2.model.PatternInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

    private static final String promptTemplateFile = "llm_sort_likert.txt";

    private final int maxSortableInstances;

    private final OllamaChatModel chatModel;

    public OllamaLikertSorter(OllamaChatModel chatModel, ApplicationConfig config) {
        this.chatModel = chatModel;
        this.maxSortableInstances = config.getLlm().getSort().getMaxInstances();
    }

    @Override
    public List<PatternInstance> sort(List<PatternInstance> patternInstances) {
        Objects.requireNonNull(patternInstances);
        if (patternInstances.size() > maxSortableInstances) {
            throw new MaxSortableInstancesThresholdExceededException("Cannot sort, maximum sortable number of pattern instances (" + maxSortableInstances + ") exceeded.");
        }
        String promptTemplate = loadPromptTemplate();
        assert !promptTemplate.isBlank();

        promptTemplate = promptTemplate.replace("$SPARQL$", patternInstances.getFirst().pattern()
                                                                            .createInsertSparqlTemplate())
                                       .replace("$VALUES$", constructValues(patternInstances));
        final Prompt prompt = Prompt.builder().content(promptTemplate).build();
        LOG.trace("Synchronously calling Ollama with prompt: '{}'.", prompt.getContents());
        final long start = System.currentTimeMillis();
        final ChatResponse resp = chatModel.call(prompt);
        final String result = resp.getResult().getOutput().getText();
        final long end = System.currentTimeMillis();
        LOG.trace("Received Ollama response: {}.", result);
        LOG.trace("Ollama invocation took {} s.", (end - start) / 1000);
        assert result != null;
        return actuallySort(patternInstances, result);
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
        return sb.toString();
    }

    private List<PatternInstance> actuallySort(List<PatternInstance> patternInstances, String llmOutput) {
        final List<PatternInstance> result = new ArrayList<>(patternInstances.size());
        final List<PatternInstanceLine> lines = llmOutput.lines().map(String::trim).filter(this::isSortResult)
                                                         .map(line -> {
                                                             final String[] items = line.split(",");
                                                             assert items.length == 3;
                                                             try {
                                                                 return new PatternInstanceLine(Integer.parseInt(items[0].trim()), Integer.parseInt(items[1].trim()));
                                                             } catch (NumberFormatException e) {
                                                                 LOG.error("Unable to parse pattern match position or Likert score from LLM response.", e);
                                                                 throw new LlmSortException("Unable to resolve match order from LLM response.");
                                                             }
                                                         })
                                                         .sorted(Comparator.comparing(PatternInstanceLine::likertScore))
                                                         .toList();
        if (lines.size() != patternInstances.size()) {
            throw new LlmSortException("LLM response does not contain equal number of items as provided pattern instances.");
        }
        for (PatternInstanceLine line : lines) {
            if (line.number() > patternInstances.size()) {
                LOG.error("LLM returned line number {} (1-based) which is greater than the number of pattern instances.", line.number());
                continue;
            }
            result.add(patternInstances.get(line.number() - 1));
        }
        return result;
    }

    private boolean isSortResult(String line) {
        return line.contains(",") && line.split(",").length == 3;
    }

    @Override
    public Sort getSortMethod() {
        return Sort.LLM_LIKERT;
    }

    private record PatternInstanceLine(int number, int likertScore) {}
}
