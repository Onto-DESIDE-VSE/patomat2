package cz.cvut.kbss.ontodeside.patomat2.model;

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

}
