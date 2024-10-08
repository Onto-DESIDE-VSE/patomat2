package cz.vse.swoe.ontodeside.patomat2.model;

import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.environment.Generator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class PatternTest {

    @Test
    void sourceSparqlGeneratesSparqlFromSourceTriples() {
        final Pattern p = new Pattern("filename.json", "name", List.of("?p rdfs:domain ?A", "?p rdfs:range ?B", "?C rdfs:subClassOf ?B"),
                List.of(), List.of(), List.of());
        assertEquals("""
                SELECT DISTINCT * WHERE {
                  ?p rdfs:domain ?A .
                  ?p rdfs:range ?B .
                  ?C rdfs:subClassOf ?B .
                }""", p.sourceSparql());
    }

    @Test
    void sourceSparqlGeneratesSparqlFromSourceTriplesAndFilters() {
        final Pattern p = new Pattern("filename.json", "name", List.of("?p rdfs:domain ?A", "?p rdfs:range ?B", "?C rdfs:subClassOf ?B"),
                List.of("FILTER (contains(str(?p), 'role'))"), List.of(), List.of());
        assertEquals("""
                SELECT DISTINCT * WHERE {
                  ?p rdfs:domain ?A .
                  ?p rdfs:range ?B .
                  ?C rdfs:subClassOf ?B .
                  FILTER (contains(str(?p), 'role'))
                }""", p.sourceSparql());
    }

    @Test
    void createTargetInsertSparqlGeneratesSparqlFromTargetTriplesAndPatternMatch() {
        final Pattern sut = new Pattern("filename.json", "name", List.of(), List.of(), List.of("?p rdfs:domain ?A",
                "?p rdfs:range ?B",
                "?C rdfs:subClassOf ?B"), List.of());
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
        )), List.of()));
    }

    @Test
    void createTargetInsertSparqlUsesNewEntitiesInTargetQuery() {
        final Pattern sut = new Pattern("filename.json", "name", List.of(), List.of(), List.of("?p rdfs:domain ?A",
                "?p rdfs:range ?B",
                "?C rdfs:subClassOf ?B",
                "?G rdfs:subClassOf ?A",
                "?G owl:equivalentClass _:restriction",
                "_:restriction rdf:type owl:Restriction",
                "_:restriction owl:onProperty ?p",
                "_:restriction owl:someValuesFrom ?C"), List.of());
        final String p = Generator.generateUri().toString();
        final String a = Generator.generateUri().toString();
        final String b = Generator.generateUri().toString();
        final String c = Generator.generateUri().toString();
        final NewEntity g = new NewEntity("G", Generator.generateUri().toString(),
                List.of(new EntityLabel("New entity", Constants.DEFAULT_LABEL_PROPERTY)));
        assertEquals("INSERT DATA {\n" +
                "  <" + p + "> rdfs:domain <" + a + "> .\n" +
                "  <" + p + "> rdfs:range <" + b + "> .\n" +
                "  <" + c + "> rdfs:subClassOf <" + b + "> .\n" +
                "  <" + g.identifier() + "> rdfs:subClassOf <" + a + "> .\n" +
                "  <" + g.identifier() + "> owl:equivalentClass _:restriction .\n" +
                "  _:restriction rdf:type owl:Restriction .\n" +
                "  _:restriction owl:onProperty <" + p + "> .\n" +
                "  _:restriction owl:someValuesFrom <" + c + "> .\n" +
                "}", sut.createTargetInsertSparql(new PatternMatch(sut, List.of(
                new ResultBinding("p", p, Constants.RDFS_RESOURCE),
                new ResultBinding("A", a, Constants.RDFS_RESOURCE),
                new ResultBinding("B", b, Constants.RDFS_RESOURCE),
                new ResultBinding("C", c, Constants.RDFS_RESOURCE)
        )), List.of(g)));
    }

    @Test
    void createTargetDeleteSparqlGeneratesSparqlFromSourceTriplesAndPatternMatch() {
        final Pattern sut = new Pattern("filename.json", "name", List.of("?p rdfs:domain ?A",
                "?p rdfs:range ?B",
                "?C rdfs:subClassOf ?B"), List.of(), List.of(), List.of());
        final String p = Generator.generateUri().toString();
        final String a = Generator.generateUri().toString();
        final String b = Generator.generateUri().toString();
        final String c = Generator.generateUri().toString();
        assertEquals("DELETE DATA {\n" +
                "  <" + p + "> rdfs:domain <" + a + "> .\n" +
                "  <" + p + "> rdfs:range <" + b + "> .\n" +
                "  <" + c + "> rdfs:subClassOf <" + b + "> .\n" +
                "}", sut.createTargetDeleteSparql(new PatternMatch(sut, List.of(
                new ResultBinding("p", p, Constants.RDFS_RESOURCE),
                new ResultBinding("A", a, Constants.RDFS_RESOURCE),
                new ResultBinding("B", b, Constants.RDFS_RESOURCE),
                new ResultBinding("C", c, Constants.RDFS_RESOURCE)
        ))));
    }

    @Test
    void createTargetDeleteSparqlReturnsNullWhenPatternMatchContainsBlankNodeBasedBinding() {
        final Pattern sut = new Pattern("filename.json", "name", List.of("?p rdfs:domain ?A",
                "?p rdfs:range ?B",
                "?C rdfs:subClassOf ?B"), List.of(), List.of(), List.of());
        final String p = Generator.generateUri().toString();
        final String a = Generator.generateUri().toString();
        final String b = Generator.generateUri().toString();
        final String c = Generator.generateUri().toString();
        assertNull(sut.createTargetDeleteSparql(new PatternMatch(sut, List.of(
                new ResultBinding("p", p, Constants.RDFS_RESOURCE),
                new ResultBinding("A", a, Constants.RDFS_RESOURCE, true),
                new ResultBinding("B", b, Constants.RDFS_RESOURCE),
                new ResultBinding("C", c, Constants.RDFS_RESOURCE)
        ))));
    }
}
