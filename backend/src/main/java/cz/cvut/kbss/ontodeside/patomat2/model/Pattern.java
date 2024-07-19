package cz.cvut.kbss.ontodeside.patomat2.model;

import cz.cvut.kbss.ontodeside.patomat2.service.rdf4j.Rdf4jSparqlQueryBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record Pattern(String name, List<String> sourceTriples, List<String> targetTriples) {

    @Override
    public List<String> sourceTriples() {
        return Collections.unmodifiableList(sourceTriples);
    }

    @Override
    public List<String> targetTriples() {
        return Collections.unmodifiableList(targetTriples);
    }

    public String sourceSparql() {
        return """
                SELECT DISTINCT * WHERE {
                %s
                }""".formatted(sourceTriples.stream().map(t -> "  " + t + " .").collect(Collectors.joining("\n")));
    }

    public String createTargetInsertSparql(PatternMatch instance) {
        final String insert = """
                INSERT DATA {
                %s
                }""".formatted(targetTriples.stream().map(t -> "  " + t + " .").collect(Collectors.joining("\n")));
        return Rdf4jSparqlQueryBuilder.populateSparqlInsert(insert, instance);
    }
}
