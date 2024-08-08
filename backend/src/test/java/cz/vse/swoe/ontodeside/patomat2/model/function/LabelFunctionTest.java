package cz.vse.swoe.ontodeside.patomat2.model.function;

import cz.vse.swoe.ontodeside.patomat2.environment.Generator;
import cz.vse.swoe.ontodeside.patomat2.exception.NameTransformationException;
import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
import cz.vse.swoe.ontodeside.patomat2.model.ResultBinding;
import cz.vse.swoe.ontodeside.patomat2.service.OntologyHolder;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LabelFunctionTest {

    @Mock
    private OntologyHolder ontologyHolder;

    @Test
    void applyCallsNextFunctionWhenRuleDoesNotContainLabelFunction() {
        final NameTransformationFunction next = mock(NameTransformationFunction.class);
        doAnswer(inv -> inv.getArgument(1)).when(next).apply(any(), any());
        final LabelFunction sut = new LabelFunction(ontologyHolder, next);
        final String rule = "?a and ?b";
        final PatternMatch match = mock(PatternMatch.class);
        final String result = sut.apply(match, rule);
        assertEquals(rule, result);
        verify(next).apply(match, rule);
        verify(ontologyHolder, never()).getLabel(any());
    }

    @Test
    void applyExtractsValueLabelFromOntologyAndReplacesVariableWithItInRule() {
        final LabelFunction sut = new LabelFunction(ontologyHolder, null);
        final String rule = "label(?a)";
        final String iri = Generator.generateUri().toString();
        when(ontologyHolder.getLabel(iri)).thenReturn(Optional.of("Test"));
        final ResultBinding binding = new ResultBinding("a", iri, "http://www.w3.org/2000/01/rdf-schema#Resource");
        final PatternMatch match = new PatternMatch(null, List.of(binding));
        final String result = sut.apply(match, rule);
        assertEquals("Test", result);
    }

    @Test
    void applyAppliesFunctionOnDifferentVariables() {
        final LabelFunction sut = new LabelFunction(ontologyHolder, null);
        final String rule = "label(?A) and label(?B)";
        final String iriA = Generator.generateUri().toString();
        final String iriB = Generator.generateUri().toString();
        when(ontologyHolder.getLabel(iriA)).thenReturn(Optional.of("Test"));
        when(ontologyHolder.getLabel(iriB)).thenReturn(Optional.of("Test2"));
        final ResultBinding bindingA = new ResultBinding("A", iriA, "http://www.w3.org/2000/01/rdf-schema#Resource");
        final ResultBinding bindingB = new ResultBinding("B", iriB, "http://www.w3.org/2000/01/rdf-schema#Resource");
        final PatternMatch match = new PatternMatch(null, List.of(bindingA, bindingB));
        final String result = sut.apply(match, rule);
        assertEquals("Test and Test2", result);
    }

    @Test
    void applyThrowsNameTransformationExceptionWhenVariableDoesNotExistInResult() {
        final LabelFunction sut = new LabelFunction(ontologyHolder, null);
        final String rule = "label(?x)";
        final ResultBinding binding = new ResultBinding("a", Generator.generateUri()
                                                                      .toString(), "http://www.w3.org/2000/01/rdf-schema#Resource");
        final PatternMatch match = new PatternMatch(null, List.of(binding));
        final NameTransformationException ex = assertThrows(NameTransformationException.class, () -> sut.apply(match, rule));
        assertThat(ex.getMessage(), containsString("Variable 'x' not found in pattern instance."));
    }

    @Test
    void applyThrowsNameTransformationExceptionWhenVariableIsNotResource() {
        final LabelFunction sut = new LabelFunction(ontologyHolder, null);
        final String rule = "label(?a)";
        final ResultBinding binding = new ResultBinding("a", "125", XSD.INT.stringValue());
        final PatternMatch match = new PatternMatch(null, List.of(binding));
        final NameTransformationException ex = assertThrows(NameTransformationException.class, () -> sut.apply(match, rule));
        assertThat(ex.getMessage(), containsString("is not a resource."));
    }

    @Test
    void applyCallsNextFunctionWithResultOfApply() {
        final NameTransformationFunction next = mock(NameTransformationFunction.class);
        doAnswer(inv -> inv.getArgument(1)).when(next).apply(any(), any());
        final LabelFunction sut = new LabelFunction(ontologyHolder, next);
        final String rule = "label(?a)";
        final String iri = Generator.generateUri().toString();
        when(ontologyHolder.getLabel(iri)).thenReturn(Optional.of("Test"));
        final ResultBinding binding = new ResultBinding("a", iri, "http://www.w3.org/2000/01/rdf-schema#Resource");
        final PatternMatch match = new PatternMatch(null, List.of(binding));
        final String result = sut.apply(match, rule);
        assertEquals("Test", result);
        verify(next).apply(match, "Test");
    }

    @Test
    void applyResolvesLabelFromValueIriWhenOntologyDoesNotContainLabel() {
        final LabelFunction sut = new LabelFunction(ontologyHolder, null);
        final String rule = "label(?a)";
        final String iri = "https://example.org/test-value";
        when(ontologyHolder.getLabel(iri)).thenReturn(Optional.empty());
        final ResultBinding binding = new ResultBinding("a", iri, "http://www.w3.org/2000/01/rdf-schema#Resource");
        final PatternMatch match = new PatternMatch(null, List.of(binding));
        final String result = sut.apply(match, rule);
        assertEquals("Test Value", result);
    }
}
