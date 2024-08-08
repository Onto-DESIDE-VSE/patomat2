package cz.cvut.kbss.ontodeside.patomat2.model;

/**
 * Difference between ontologies.
 * <p>
 * Represented by added and removed statements serialized as n-triples.
 *
 * @param addedStatements Added statements
 * @param removedStatements Removed statements
 */
public record OntologyDiff(String addedStatements, String removedStatements) {
}
