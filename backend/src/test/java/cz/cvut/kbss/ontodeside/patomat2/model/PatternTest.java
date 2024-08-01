package cz.cvut.kbss.ontodeside.patomat2.model;

import cz.cvut.kbss.ontodeside.patomat2.Constants;
import cz.cvut.kbss.ontodeside.patomat2.environment.Generator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PatternTest {

    @Test
    void sourceSparqlGeneratesSparqlFromSourceTriples() {
        final Pattern p = new Pattern("filename.json", "name", List.of("?p rdfs:domain ?A", "?p rdfs:range ?B", "?C rdfs:subClassOf ?B"),
                List.of(), List.of());
        assertEquals("""
                SELECT DISTINCT * WHERE {
                  ?p rdfs:domain ?A .
                  ?p rdfs:range ?B .
                  ?C rdfs:subClassOf ?B .
                }""", p.sourceSparql());
    }

    @Test
    void createTargetInsertSparqlGeneratesSparqlFromTargetTriplesAndPatternMatch() {
        final Pattern sut = new Pattern("filename.json", "name", List.of(), List.of("?p rdfs:domain ?A",
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
        final Pattern sut = new Pattern("filename.json","name", List.of(), List.of("?p rdfs:domain ?A",
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
        final NewEntity g = new NewEntity("G", Generator.generateUri().toString(), "New entity");
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
        final Pattern sut = new Pattern("filename.json","name", List.of("?p rdfs:domain ?A",
                "?p rdfs:range ?B",
                "?C rdfs:subClassOf ?B"), List.of(), List.of());
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
}
