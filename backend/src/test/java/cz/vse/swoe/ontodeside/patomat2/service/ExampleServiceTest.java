package cz.vse.swoe.ontodeside.patomat2.service;

import cz.vse.swoe.ontodeside.patomat2.config.ApplicationConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExampleServiceTest {

    @Test
    void hasExampleReturnsTrueWhenOntologyAndAtLeastOnePatternAreConfigured() {
        final ApplicationConfig config = new ApplicationConfig();
        config.setExample(new ApplicationConfig.Example("http://example.com/ontology.ttl", List.of("http://example.com/pattern.json")));
        final ExampleService sut = new ExampleService(config);
        assertTrue(sut.hasExample());
    }

    @Test
    void hasExampleReturnsFalseWhenExampleOntologyIsNotConfigured() {
        final ApplicationConfig config = new ApplicationConfig();
        config.setExample(new ApplicationConfig.Example(null, List.of("http://example.com/pattern.json")));
        final ExampleService sut = new ExampleService(config);
        assertFalse(sut.hasExample());
    }

    @Test
    void hasExampleReturnsFalseWhenExamplePatternIsNotConfigured() {
        final ApplicationConfig config = new ApplicationConfig();
        config.setExample(new ApplicationConfig.Example("http://example.com/ontology.ttl", List.of()));
        final ExampleService sut = new ExampleService(config);
        assertFalse(sut.hasExample());
    }
}
