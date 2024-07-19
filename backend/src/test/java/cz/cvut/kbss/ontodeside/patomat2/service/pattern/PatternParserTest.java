package cz.cvut.kbss.ontodeside.patomat2.service.pattern;

import cz.cvut.kbss.ontodeside.patomat2.model.Pattern;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatternParserTest {

    private final PatternParser sut = new PatternParser();

    @Test
    void readPatternParsesPatternNameAndTriples() {
        final Pattern p = sut.readPattern(new File("src/test/resources/pattern-example.json"));
        assertEquals("CAT1", p.getName());
        assertEquals(List.of("?p rdfs:domain ?A", "?p rdfs:range ?B", "?C rdfs:subClassOf ?B"), p.getSourceTriples());
        assertEquals(List.of("?p rdfs:domain ?A",
                "?p rdfs:range ?B",
                "?C rdfs:subClassOf ?B",
                "?G rdfs:subClassOf ?A",
                "?G owl:equivalentClass _:restriction",
                "_:restriction rdf:type owl:Restriction",
                "_:restriction owl:onProperty ?p",
                "_:restriction owl:someValuesFrom ?C"), p.getTargetTriples());
    }
}
