package cz.cvut.kbss.ontodeside.patomat2.model;

import java.net.URI;

/**
 * Generates new entities (RDF resources) to be inserted as part of transformation.
 */
public interface NewEntityGenerator {

    /**
     * Generates a new identifier for an entity.
     *
     * @return New entity identifier
     */
    URI generateIdentifier();
}
