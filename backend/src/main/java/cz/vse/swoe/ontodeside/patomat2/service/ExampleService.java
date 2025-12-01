package cz.vse.swoe.ontodeside.patomat2.service;

import cz.vse.swoe.ontodeside.patomat2.config.ApplicationConfig;
import cz.vse.swoe.ontodeside.patomat2.exception.NotFoundException;
import cz.vse.swoe.ontodeside.patomat2.model.TransformationInput;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Manages access to transformation examples.
 */
@Service
public class ExampleService {

    private final OntologyStoringService ontologyStoringService;

    private final List<ApplicationConfig.Example> examples;

    public ExampleService(OntologyStoringService ontologyStoringService, ApplicationConfig config) {
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
     * Loads the ontology and patterns specified in example with the given name.
     *
     * @param exampleName Name of the example to load
     */
    public void loadExample(String exampleName) {
        final Optional<ApplicationConfig.Example> example = examples.stream()
                                                                    .filter(e -> e.getName().equals(exampleName))
                                                                    .findFirst();
        if (example.isEmpty()) {
            throw new NotFoundException("Example with name '" + exampleName + "' not found.");
        }
        final TransformationInput input = new TransformationInput(example.get().getOntology(),
                example.get().getPatterns(), example.get().isResolveImports());
        ontologyStoringService.saveOntologyAndPatterns(input);
    }
}
