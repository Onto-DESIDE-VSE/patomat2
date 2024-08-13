package cz.vse.swoe.ontodeside.patomat2.model;

import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.environment.Generator;
import cz.vse.swoe.ontodeside.patomat2.exception.NameTransformationException;
import cz.vse.swoe.ontodeside.patomat2.service.OntologyHolder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NameTransformationTest {

    @Mock
    private OntologyHolder ontologyHolder;

    @Test
    void generateNameUsesLocalPartOfIdentifierWhenResourceHasNoLabel() {
        final NameTransformation sut = new NameTransformation("G", "?A that ?p a ?C");
        final String p = FOAF.KNOWS.stringValue();
        final String a = "https://example.com/users/john-doe";
        final String c = SKOS.CONCEPT.stringValue();
        final PatternMatch match = new PatternMatch(null, List.of(
                new ResultBinding("p", p, Constants.RDFS_RESOURCE),
                new ResultBinding("A", a, Constants.RDFS_RESOURCE),
                new ResultBinding("C", c, Constants.RDFS_RESOURCE)
        ));
        final String result = sut.generateName(match, ontologyHolder);
        assertEquals("John Doe that Knows a Concept", result);
    }

    @Test
    void generateNameThrowsNameTransformationExceptionWhenRuleVariableIsMissingValueInPatternMatch() {
        final NameTransformation sut = new NameTransformation("G", "?A that ?p a ?C");
        final String p = FOAF.KNOWS.stringValue();
        final String a = "https://example.com/users/john-doe";
        final PatternMatch match = new PatternMatch(null, List.of(
                new ResultBinding("p", p, Constants.RDFS_RESOURCE),
                new ResultBinding("A", a, Constants.RDFS_RESOURCE)
        ));
        assertThrows(NameTransformationException.class, () -> sut.generateName(match, ontologyHolder));
    }

    @Test
    void generateReplacesCamelCaseWordsInIdentifierWithSpaces() {
        final NameTransformation sut = new NameTransformation("G", "?A that ?p a ?C");
        final String p = FOAF.KNOWS.stringValue();
        final String a = "https://example.com/users/john-doe";
        final String c = SKOS.TOP_CONCEPT_OF.stringValue();
        final PatternMatch match = new PatternMatch(null, List.of(
                new ResultBinding("p", p, Constants.RDFS_RESOURCE),
                new ResultBinding("A", a, Constants.RDFS_RESOURCE),
                new ResultBinding("C", c, Constants.RDFS_RESOURCE)
        ));
        final String result = sut.generateName(match, ontologyHolder);
        assertEquals("John Doe that Knows a Top Concept Of", result);
    }

    @Test
    void generateHandlesPartsWithCapitalizedUriComponents() {
        final NameTransformation sut = new NameTransformation("G", "?A that ?p a ?C");
        final String p = FOAF.KNOWS.stringValue();
        final String a = "http://cmt/#Meta-Reviewer";
        final String c = SKOS.TOP_CONCEPT_OF.stringValue();
        final PatternMatch match = new PatternMatch(null, List.of(
                new ResultBinding("p", p, Constants.RDFS_RESOURCE),
                new ResultBinding("A", a, Constants.RDFS_RESOURCE),
                new ResultBinding("C", c, Constants.RDFS_RESOURCE)
        ));
        final String result = sut.generateName(match, ontologyHolder);
        assertEquals("Meta Reviewer that Knows a Top Concept Of", result);
    }

    @Test
    void generateAppliesLabelFunctionDeclaredInRule() {
        final NameTransformation sut = new NameTransformation("G", "?A that label(?p) a ?C");
        final String p = Generator.generateUri().toString();
        final String pLabel = "knows";
        final String a = "https://example.com/users/john-doe";
        final String c = SKOS.TOP_CONCEPT_OF.stringValue();
        final PatternMatch match = new PatternMatch(null, List.of(
                new ResultBinding("p", p, Constants.RDFS_RESOURCE),
                new ResultBinding("A", a, Constants.RDFS_RESOURCE),
                new ResultBinding("C", c, Constants.RDFS_RESOURCE)
        ));
        when(ontologyHolder.getLabel(p)).thenReturn(Optional.of(pLabel));
        final String result = sut.generateName(match, ontologyHolder);
        assertEquals("John Doe that knows a Top Concept Of", result);
    }

    @Test
    void applyAppliesFunctionOnDifferentVariables() {
        final NameTransformation sut = new NameTransformation("G", "label(?A) and label(?B)");
        final String iriA = Generator.generateUri().toString();
        final String iriB = Generator.generateUri().toString();
        when(ontologyHolder.getLabel(iriA)).thenReturn(Optional.of("Test"));
        when(ontologyHolder.getLabel(iriB)).thenReturn(Optional.of("Test2"));
        final ResultBinding bindingA = new ResultBinding("A", iriA, "http://www.w3.org/2000/01/rdf-schema#Resource");
        final ResultBinding bindingB = new ResultBinding("B", iriB, "http://www.w3.org/2000/01/rdf-schema#Resource");
        final PatternMatch match = new PatternMatch(null, List.of(bindingA, bindingB));
        final String result = sut.generateName(match, ontologyHolder);
        assertEquals("Test and Test2", result);
    }
}
