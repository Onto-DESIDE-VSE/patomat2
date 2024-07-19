package cz.cvut.kbss.ontodeside.patomat2.model;

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
}
