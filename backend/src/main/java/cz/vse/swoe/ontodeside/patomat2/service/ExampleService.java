package cz.vse.swoe.ontodeside.patomat2.service;

import cz.vse.swoe.ontodeside.patomat2.config.ApplicationConfig;
import org.springframework.stereotype.Service;

/**
 * Manages access to transformation example.
 */
@Service
public class ExampleService {

    private final ApplicationConfig.Example example;

    public ExampleService(ApplicationConfig config) {
        this.example = config.getExample();
    }

    /**
     * Checks whether an example transformation input is available.
     * <p>
     * An ontology and at least one pattern must be configured to consider the example available.
     *
     * @return {@code true} when an example is available, {@code false} otherwise
     */
    public boolean hasExample() {
        return example.ontology() != null && !example.ontology().isBlank()
                && example.patterns() != null && example.patterns().stream().anyMatch(s -> !s.isBlank());
    }
}
