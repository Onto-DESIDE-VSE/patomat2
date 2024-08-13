package cz.vse.swoe.ontodeside.patomat2.service.pattern;

import cz.vse.swoe.ontodeside.patomat2.model.NameTransformation;
import cz.vse.swoe.ontodeside.patomat2.model.Pattern;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatternParserTest {

    private final PatternParser sut = new PatternParser();

    @Test
    void readPatternParsesPatternNameAndTriples() {
        final Pattern p = sut.readPattern(new File("src/test/resources/pattern-example.json"));
        assertEquals("CAT1", p.name());
        assertEquals(List.of("?p rdfs:domain ?A", "?p rdfs:range ?B", "?C rdfs:subClassOf ?B"), p.sourceTriples());
        assertEquals(List.of("?p rdfs:domain ?A",
                "?p rdfs:range ?B",
                "?C rdfs:subClassOf ?B",
                "?G rdfs:subClassOf ?A",
                "?G owl:equivalentClass _:restriction",
                "_:restriction rdf:type owl:Restriction",
                "_:restriction owl:onProperty ?p",
                "_:restriction owl:someValuesFrom ?C"), p.targetTriples());
    }

    @Test
    void readPatternParsesFilters() {
        final Pattern p = sut.readPattern(new File("src/test/resources/pattern-example.json"));
        assertEquals(List.of("FILTER(contains(str(?p), 'role'))"), p.filters());
    }

    @Test
    void readPatternParsesNameTransformations() {
        final Pattern p = sut.readPattern(new File("src/test/resources/pattern-example.json"));
        assertEquals(List.of(new NameTransformation("G", "?A that ?p a ?C")), p.nameTransformations());
    }

    @Test
    void readPatternParsesMultipleNameTransformationsForOneVariable() {
        final Pattern p = sut.readPattern(new File("src/test/resources/pattern-example-multiple-ntp.json"));
        assertEquals(List.of(new NameTransformation("G", "?A that ?p a ?C"), new NameTransformation("G", "?p ?C")), p.nameTransformations());
    }
}
