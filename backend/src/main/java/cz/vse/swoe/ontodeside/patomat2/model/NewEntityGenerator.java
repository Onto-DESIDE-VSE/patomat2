package cz.vse.swoe.ontodeside.patomat2.model;

import cz.vse.swoe.ontodeside.patomat2.model.iri.NewEntityIriConfig;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * Generates new entities (RDF resources) to be inserted as part of transformation.
 */
public interface NewEntityGenerator {

    /**
     * Generates a new identifier for an entity with the specified labels.
     *
     * @return New entity identifier
     */
    String generateIdentifier(List<EntityLabel> labels);

    /**
     * Generates a new entity with label(s) based on the specified pattern match and name transformations.
     * <p>
     * A new identifier is generated for the entity.
     * <p>
     * There can be multiple name transformations specified for the entity, causing multiple labels to be generated for
     * it.
     *
     * @param variableName        Name of the variable that is to be replaced by the new entity
     * @param nameTransformations Definition of how the new entity's label(s) should be created
     * @param match               Pattern instance
     * @return New entity
     */
    NewEntity generateNewEntity(@NonNull String variableName, @NonNull List<NameTransformation> nameTransformations,
                                @NonNull PatternMatch match);

    /**
     * Sets configuration for generating new entity identifiers.
     *
     * @param iriConfig Configuration to set
     */
    void setIriConfig(NewEntityIriConfig iriConfig);

    /**
     * Gets the current configuration for generating new entity identifiers.
     *
     * @return Current configuration
     */
    NewEntityIriConfig getIriConfig();

    /**
     * Initializes new entity identifier generation configuration.
     */
    void initNewEntityIriConfig();
}
