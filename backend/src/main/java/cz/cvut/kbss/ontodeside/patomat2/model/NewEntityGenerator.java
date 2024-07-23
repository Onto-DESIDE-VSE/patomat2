package cz.cvut.kbss.ontodeside.patomat2.model;

import org.springframework.lang.NonNull;

/**
 * Generates new entities (RDF resources) to be inserted as part of transformation.
 */
public interface NewEntityGenerator {

    /**
     * Generates a new identifier for an entity.
     *
     * @return New entity identifier
     */
    String generateIdentifier();

    /**
     * Generates a new entity with label based on the specified pattern match and name transformation.
     * <p>
     * A new identifier is generated for the entity.
     *
     * @param variableName       Name of the variable that is to be replaced by the new entity
     * @param nameTransformation Definition of how the new entity's label should be created
     * @param match              Pattern instance
     * @return New entity
     */
    NewEntity generateNewEntity(@NonNull String variableName, @NonNull NameTransformation nameTransformation,
                                @NonNull PatternMatch match);
}
