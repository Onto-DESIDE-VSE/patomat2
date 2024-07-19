package cz.cvut.kbss.ontodeside.patomat2.service;

import cz.cvut.kbss.ontodeside.patomat2.model.Pattern;
import cz.cvut.kbss.ontodeside.patomat2.model.PatternMatch;
import org.springframework.lang.NonNull;

import java.io.File;
import java.util.List;

/**
 * Manages ontology and allows finding matches in it.
 */
public interface OntologyHolder {

    /**
     * Whether ontology from the specified file has been loaded.
     *
     * @param fileName Ontology file name
     * @return {@code true} if ontology from the specified file has been loaded
     */
    boolean isLoaded(String fileName);

    /**
     * Loads ontology from the specified file.
     *
     * @param ontologyFile File containing ontology
     */
    void loadOntology(File ontologyFile);

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
