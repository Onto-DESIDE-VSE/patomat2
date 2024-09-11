package cz.vse.swoe.ontodeside.patomat2.service;

import cz.vse.swoe.ontodeside.patomat2.config.ApplicationConfig;
import cz.vse.swoe.ontodeside.patomat2.exception.NotFoundException;
import cz.vse.swoe.ontodeside.patomat2.model.Pattern;
import cz.vse.swoe.ontodeside.patomat2.model.PatternInstance;
import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExampleServiceTest {

    @Mock
    private MatchService matchService;

    @Mock
    private OntologyStoringService storingService;

    @Test
    void getExamplesReturnsNamesOfConfiguredExamples() {
        final ApplicationConfig config = new ApplicationConfig();
        config.setExamples(List.of(new ApplicationConfig.Example("test", "http://example.com/ontology.ttl", List.of("http://example.com/pattern.json"))));
        final ExampleService sut = new ExampleService(matchService, storingService, config);

        assertEquals(List.of("test"), sut.getExamples());
    }

    @Test
    void getExampleMatchesStoresExampleFilesAndGetsMatches() {
        final ApplicationConfig config = new ApplicationConfig();
        config.setExamples(List.of(new ApplicationConfig.Example("test", "http://example.com/ontology.ttl", List.of("http://example.com/pattern.json"))));
        final ExampleService sut = new ExampleService(matchService, storingService, config);
        final PatternInstance inst = new PatternInstance(1, "test", new PatternMatch(new Pattern("pattern.json", "test", List.of(), List.of(), List.of(), List.of()), List.of()), "INSERT", "DELETE", List.of());
        when(matchService.findMatches()).thenReturn(List.of(inst));

        final List<PatternInstance> result = sut.getExampleMatches("test");
        assertEquals(List.of(inst), result);
        verify(storingService).saveOntologyAndPatterns(new TransformationInput("http://example.com/ontology.ttl", List.of("http://example.com/pattern.json")));
        verify(matchService).findMatches();
    }

    @Test
    void getExampleMatchesThrowsNotFoundExceptionWhenNoSuchExampleExists() {
        final ApplicationConfig config = new ApplicationConfig();
        config.setExamples(List.of(new ApplicationConfig.Example("test", "http://example.com/ontology.ttl", List.of("http://example.com/pattern.json"))));
        final ExampleService sut = new ExampleService(matchService, storingService, config);

        assertThrows(NotFoundException.class, () -> sut.getExampleMatches("unknown"));
        verify(storingService, never()).saveOntologyAndPatterns(any());
        verify(matchService, never()).findMatches();
    }
}
