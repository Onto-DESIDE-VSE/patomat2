package cz.vse.swoe.ontodeside.patomat2.service;

import cz.vse.swoe.ontodeside.patomat2.config.ApplicationConfig;
import cz.vse.swoe.ontodeside.patomat2.exception.NotFoundException;
import cz.vse.swoe.ontodeside.patomat2.model.TransformationInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExampleServiceTest {
    @Mock
    private OntologyStoringService storingService;

    @Test
    void getExamplesReturnsNamesOfConfiguredExamples() {
        final ApplicationConfig config = new ApplicationConfig();
        config.setExamples(List.of(new ApplicationConfig.Example("test", "http://example.com/ontology.ttl", List.of("http://example.com/pattern.json"))));
        final ExampleService sut = new ExampleService(storingService, config);

        assertEquals(List.of("test"), sut.getExamples());
    }

    @Test
    void loadExampleLoadsExamplesFromUrls() {
        final ApplicationConfig config = new ApplicationConfig();
        config.setExamples(List.of(new ApplicationConfig.Example("test", "http://example.com/ontology.ttl", List.of("http://example.com/pattern.json"))));
        final ExampleService sut = new ExampleService(storingService, config);

        sut.loadExample("test");
        verify(storingService).saveOntologyAndPatterns(new TransformationInput("http://example.com/ontology.ttl", List.of("http://example.com/pattern.json")));
    }

    @Test
    void loadExampleThrowsNotFoundExceptionWhenNoSuchExampleExists() {
        final ApplicationConfig config = new ApplicationConfig();
        config.setExamples(List.of(new ApplicationConfig.Example("test", "http://example.com/ontology.ttl", List.of("http://example.com/pattern.json"))));
        final ExampleService sut = new ExampleService(storingService, config);

        assertThrows(NotFoundException.class, () -> sut.loadExample("unknown"));
        verify(storingService, never()).saveOntologyAndPatterns(any());
    }
}
