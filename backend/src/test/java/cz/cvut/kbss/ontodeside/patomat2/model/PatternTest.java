package cz.cvut.kbss.ontodeside.patomat2.model;

import cz.cvut.kbss.ontodeside.patomat2.Constants;
import cz.cvut.kbss.ontodeside.patomat2.environment.Generator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PatternTest {

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
                "}", sut.createTargetInsertSparql(new PatternMatch("test", List.of(
                new ResultBinding("p", p, Constants.RDFS_RESOURCE),
                new ResultBinding("A", a, Constants.RDFS_RESOURCE),
                new ResultBinding("B", b, Constants.RDFS_RESOURCE),
                new ResultBinding("C", c, Constants.RDFS_RESOURCE)
        ))));
    }
}
