package cz.cvut.kbss.ontodeside.patomat2.service;

import cz.cvut.kbss.ontodeside.patomat2.model.Pattern;
import cz.cvut.kbss.ontodeside.patomat2.model.PatternMatch;
import org.springframework.lang.NonNull;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Manages ontology and allows finding matches in it.
 */
public interface OntologyHolder {

    /**
     * Checks whether any ontology has been loaded by this holder.
     *
     * @return {@code true} if an ontology has been loaded
     */
    boolean isLoaded();

    /**
     * Whether ontology from the specified file has been loaded.
     *
     * @param fileName Ontology file name
     * @return {@code true} if ontology from the specified file has been loaded
     */
    boolean isLoaded(@NonNull String fileName);

    /**
     * Loads ontology from the specified file.
     *
     * @param ontologyFile File containing ontology
     */
    void loadOntology(@NonNull File ontologyFile);

    /**
     * Resolves IRI of the loaded ontology, if available.
     *
     * @return Ontology IRI, possibly empty
     */
    Optional<String> getOntologyIri();

    /**
     * Resolves label of a resource with the specified iri.
     *
     * @param iri Resource identifier
     * @return Resource label (if found)
     */
    Optional<String> getLabel(@NonNull String iri);

    /**
     * Finds matches of the specified pattern in the loaded ontology.
     *
     * @param pattern Pattern definition
     * @return List of found matches
     */
    List<PatternMatch> findMatches(@NonNull Pattern pattern);

    /**
     * Clears the holder, discarding the previously loaded ontology.
     */
    void clear();
}
