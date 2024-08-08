package cz.vse.swoe.ontodeside.patomat2.model;

import org.springframework.core.io.Resource;

/**
 * Result of transformation.
 *
 * @param ontology The transformed ontology
 * @param summary  Summary of added and removed statements
 */
public record TransformationResult(Resource ontology, TransformationSummary summary) {
}
