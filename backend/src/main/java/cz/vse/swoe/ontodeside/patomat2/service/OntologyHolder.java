package cz.vse.swoe.ontodeside.patomat2.service;

import cz.vse.swoe.ontodeside.patomat2.model.OntologyDiff;
import cz.vse.swoe.ontodeside.patomat2.model.Pattern;
import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
import org.springframework.lang.NonNull;

import java.io.ByteArrayOutputStream;
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
     * Returns true if the ontology was loaded including its imports.
     *
     * @return {@code true} if ontology was loaded with imports
     * @see #loadOntology(File, boolean)
     */
    boolean isLoadedWithImports();

    /**
     * Loads ontology from the specified file.
     *
     * @param ontologyFile   File containing ontology
     * @param resolveImports Whether to resolve ontology imports ({@literal owl:imports})
     */
    void loadOntology(@NonNull File ontologyFile, boolean resolveImports);

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
     * Applies the specified SPARQL Update to the loaded ontology.
     *
     * @param sparqlUpdate SPARQL Update to execute
     */
    void applyTransformationQuery(@NonNull String sparqlUpdate);

    /**
     * Exports the currently loaded ontology.
     *
     * @param mimeType MIME type of the exported ontology (e.g. "text/turtle")
     * @return Exported ontology
     */
    ByteArrayOutputStream export(@NonNull String mimeType);

    /**
     * Clears the holder, discarding the previously loaded ontology.
     */
    void clear();

    /**
     * Calculates the difference of the loaded and the specified ontologies.
     *
     * @param otherOntologyFile File containing the other ontology
     * @return Ontology difference
     */
    OntologyDiff difference(@NonNull File otherOntologyFile);
}
