package cz.cvut.kbss.ontodeside.patomat2.model;

import cz.cvut.kbss.ontodeside.patomat2.Constants;
import cz.cvut.kbss.ontodeside.patomat2.util.Rdf4jSparqlQueryBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
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

    /**
     * Creates a SPARQL SELECT query to find instances of this pattern in the source ontology.
     * <p>
     * The source triples are used to generate the query.
     *
     * @return SPARQL SELECT query
     */
    public String sourceSparql() {
        return """
                SELECT DISTINCT * WHERE {
                %s
                }""".formatted(sourceTriples.stream().map(t -> "  " + t + " .").collect(Collectors.joining("\n")));
    }

    /**
     * Creates a SPARQL INSERT query to insert data corresponding to the specified pattern match into the target
     * ontology.
     * <p>
     * The target triples are used to generate the query, being populated by the specified pattern match.
     *
     * @param instance Data to insert
     * @return SPARQL INSERT query
     */
    public String createTargetInsertSparql(PatternMatch instance, NewEntityGenerator newEntityGenerator) {
        final Set<String> expectedVariables = getVariables(targetTriples);
        final Set<String> actualVariables = instance.getVariables();
        final Set<String> newEntities = new HashSet<>(expectedVariables);
        newEntities.removeAll(actualVariables);
        final PatternMatch instanceWithNewEntities = new PatternMatch(instance.getPattern(), instance.getBindings());
        for (String name : newEntities) {
            instanceWithNewEntities.addBinding(name, newEntityGenerator.generateIdentifier()
                                                                       .toString(), Constants.RDFS_RESOURCE);
        }
        final String insert = """
                INSERT DATA {
                %s
                }""".formatted(targetTriples.stream().map(t -> "  " + t + " .").collect(Collectors.joining("\n")));
        return Rdf4jSparqlQueryBuilder.populateSparqlInsert(insert, instanceWithNewEntities);
    }

    /**
     * Extracts distinct SPARQL variable names from the specified triple patterns.
     * <p>
     * Note that the variable extraction is not 100% per SPARQL standard, but should cover the most common cases.
     *
     * @param triplePatterns Triple patterns to process
     * @return Set of SPARQL variables found in the triple patterns
     */
    private static Set<String> getVariables(List<String> triplePatterns) {
        final java.util.regex.Pattern variableRegex = java.util.regex.Pattern.compile("\\?([a-zA-Z0-9_]+)(?=\\W)");
        return triplePatterns.stream().map(s -> {
            final Matcher matcher = variableRegex.matcher(s);
            final Set<String> vars = new HashSet<>();
            while (matcher.find()) {
                vars.add(matcher.group(1));
            }
            return vars;
        }).flatMap(Set::stream).collect(Collectors.toSet());
    }
}
