package cz.cvut.kbss.ontodeside.patomat2.model;

import cz.cvut.kbss.ontodeside.patomat2.Constants;
import cz.cvut.kbss.ontodeside.patomat2.util.Rdf4jSparqlQueryBuilder;
import cz.cvut.kbss.ontodeside.patomat2.util.Utils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record Pattern(String name, List<String> sourceTriples, List<String> targetTriples,
                      List<NameTransformation> nameTransformations) {

    @Override
    public List<String> sourceTriples() {
        return Collections.unmodifiableList(sourceTriples);
    }

    @Override
    public List<String> targetTriples() {
        return Collections.unmodifiableList(targetTriples);
    }

    @Override
    public List<NameTransformation> nameTransformations() {
        return Collections.unmodifiableList(nameTransformations);
    }

    public Set<String> sourceVariables() {
        return getVariables(sourceTriples);
    }

    public Set<String> targetVariables() {
        return getVariables(targetTriples);
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
     * @param instance    Data to insert
     * @param newEntities List of new entities to create
     * @return SPARQL INSERT query
     */
    public String createTargetInsertSparql(PatternMatch instance, List<NewEntity> newEntities) {
        final PatternMatch instanceWithNewEntities = new PatternMatch(instance.getPattern(), instance.getBindings());
        for (NewEntity newEntity : newEntities) {
            instanceWithNewEntities.addBinding(newEntity.variableName(), newEntity.identifier(), Constants.RDFS_RESOURCE);
        }
        final String insert = """
                INSERT DATA {
                %s
                }""".formatted(targetTriples.stream().map(t -> "  " + t + " .").collect(Collectors.joining("\n")));
        return Rdf4jSparqlQueryBuilder.populateSparqlUpdate(insert, instanceWithNewEntities);
    }

    /**
     * Creates a SPARQL DELETE query to delete data corresponding to the specified pattern match from the target
     * ontology.
     *
     * @param instance Pattern instance to delete
     * @return SPARQL DELETE query
     */
    public String createTargetDeleteSparql(PatternMatch instance) {
        final String delete = """
                DELETE DATA {
                %s
                }""".formatted(sourceTriples.stream().map(t -> "  " + t + " .").collect(Collectors.joining("\n")));
        return Rdf4jSparqlQueryBuilder.populateSparqlUpdate(delete, instance);
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
        return triplePatterns.stream().map(Utils::extractSparqlVariables).flatMap(Set::stream)
                             .collect(Collectors.toSet());
    }
}
