package cz.cvut.kbss.ontodeside.patomat2.model;

import cz.cvut.kbss.ontodeside.patomat2.Constants;
import cz.cvut.kbss.ontodeside.patomat2.environment.Generator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatternTest {

    @Mock
    private NewEntityGenerator newEntityGenerator;

    @Test
    void sourceSparqlGeneratesSparqlFromSourceTriples() {
        final Pattern p = new Pattern("name", List.of("?p rdfs:domain ?A", "?p rdfs:range ?B", "?C rdfs:subClassOf ?B"), List.of());
        assertEquals("""
                SELECT DISTINCT * WHERE {
                  ?p rdfs:domain ?A .
                  ?p rdfs:range ?B .
                  ?C rdfs:subClassOf ?B .
                }""", p.sourceSparql());
    }

    @Test
    void createTargetInsertSparqlGeneratesSparqlFromTargetTriplesAndPatternMatch() {
        final Pattern sut = new Pattern("name", List.of(), List.of("?p rdfs:domain ?A",
                "?p rdfs:range ?B",
                "?C rdfs:subClassOf ?B"));
        final String p = Generator.generateUri().toString();
        final String a = Generator.generateUri().toString();
        final String b = Generator.generateUri().toString();
        final String c = Generator.generateUri().toString();
        assertEquals("INSERT DATA {\n" +
                "  <" + p + "> rdfs:domain <" + a + "> .\n" +
                "  <" + p + "> rdfs:range <" + b + "> .\n" +
                "  <" + c + "> rdfs:subClassOf <" + b + "> .\n" +
                "}", sut.createTargetInsertSparql(new PatternMatch(sut, List.of(
                new ResultBinding("p", p, Constants.RDFS_RESOURCE),
                new ResultBinding("A", a, Constants.RDFS_RESOURCE),
                new ResultBinding("B", b, Constants.RDFS_RESOURCE),
                new ResultBinding("C", c, Constants.RDFS_RESOURCE)
        )), newEntityGenerator));
    }

    @Test
    void createTargetInsertSparqlGeneratesIdentifiersOfNewEntitiesToUseInTargetQuery() {
        final Pattern sut = new Pattern("name", List.of(), List.of("?p rdfs:domain ?A",
                "?p rdfs:range ?B",
                "?C rdfs:subClassOf ?B",
                "?G rdfs:subClassOf ?A",
                "?G owl:equivalentClass _:restriction",
                "_:restriction rdf:type owl:Restriction",
                "_:restriction owl:onProperty ?p",
                "_:restriction owl:someValuesFrom ?C"));
        final String p = Generator.generateUri().toString();
        final String a = Generator.generateUri().toString();
        final String b = Generator.generateUri().toString();
        final String c = Generator.generateUri().toString();
        final URI g = Generator.generateUri();
        when(newEntityGenerator.generateIdentifier()).thenReturn(g);
        assertEquals("INSERT DATA {\n" +
                "  <" + p + "> rdfs:domain <" + a + "> .\n" +
                "  <" + p + "> rdfs:range <" + b + "> .\n" +
                "  <" + c + "> rdfs:subClassOf <" + b + "> .\n" +
                "  <" + g + "> rdfs:subClassOf <" + a + "> .\n" +
                "  <" + g + "> owl:equivalentClass _:restriction .\n" +
                "  _:restriction rdf:type owl:Restriction .\n" +
                "  _:restriction owl:onProperty <" + p + "> .\n" +
                "  _:restriction owl:someValuesFrom <" + c + "> .\n" +
                "}", sut.createTargetInsertSparql(new PatternMatch(sut, List.of(
                new ResultBinding("p", p, Constants.RDFS_RESOURCE),
                new ResultBinding("A", a, Constants.RDFS_RESOURCE),
                new ResultBinding("B", b, Constants.RDFS_RESOURCE),
                new ResultBinding("C", c, Constants.RDFS_RESOURCE)
        )), newEntityGenerator));
        verify(newEntityGenerator).generateIdentifier();
    }
}
