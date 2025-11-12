package cz.vse.swoe.ontodeside.patomat2.model.iri;

/**
 * Configuration of new entity IRI generation.
 *
 * @param namespace       IRI namespace
 * @param localNameFormat Local name format
 */
public record NewEntityIriConfig(String namespace, EntityIriLocalNameFormat localNameFormat) {
}
