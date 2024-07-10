package cz.cvut.kbss.ontodeside.patomat2.service;

import cz.cvut.kbss.ontodeside.patomat2.dto.PatternMatch;

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
     * Finds matches of the pattern declared in the specified file in the loaded ontology.
     *
     * @param patternFile File containing pattern definition
     * @return List of found matches
     */
    List<PatternMatch> findMatches(File patternFile);
}
