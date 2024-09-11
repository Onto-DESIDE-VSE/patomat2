package cz.vse.swoe.ontodeside.patomat2.service;

import cz.vse.swoe.ontodeside.patomat2.config.ApplicationConfig;
import cz.vse.swoe.ontodeside.patomat2.exception.NotFoundException;
import cz.vse.swoe.ontodeside.patomat2.model.PatternInstance;
import cz.vse.swoe.ontodeside.patomat2.model.TransformationInput;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Manages access to transformation examples.
 */
@Service
public class ExampleService {

    private final MatchService matchService;

    private final OntologyStoringService ontologyStoringService;

    private final List<ApplicationConfig.Example> examples;

    public ExampleService(MatchService matchService, OntologyStoringService ontologyStoringService,
                          ApplicationConfig config) {
        this.matchService = matchService;
        this.ontologyStoringService = ontologyStoringService;
        this.examples = config.getExamples();
    }

    /**
     * Gets a list of available examples.
     *
     * @return List of example names
     */
    public List<String> getExamples() {
        return examples.stream().map(ApplicationConfig.Example::getName).toList();
    }

    /**
     * Gets matches of the example patterns in the example ontology.
     *
     * @return Example pattern matches
     */
    public List<PatternInstance> getExampleMatches(String exampleName) {
        final Optional<ApplicationConfig.Example> example = examples.stream()
                                                                    .filter(e -> e.getName().equals(exampleName))
                                                                    .findFirst();
        if (example.isEmpty()) {
            throw new NotFoundException("Example with name '" + exampleName + "' not found.");
        }
        final TransformationInput input = new TransformationInput(example.get().getOntology(), example.get()
                                                                                                      .getPatterns());
        ontologyStoringService.saveOntologyAndPatterns(input);
        return matchService.findMatches();
    }
}
