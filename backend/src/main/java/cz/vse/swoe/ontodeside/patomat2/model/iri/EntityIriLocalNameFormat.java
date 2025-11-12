package cz.vse.swoe.ontodeside.patomat2.model.iri;

/**
 * Format of the local name of an entity IRI.
 */
public enum EntityIriLocalNameFormat {
    /**
     * Random number appended to the namespace
     */
    RANDOM_NUMBER,
    /**
     * UUID appended to the namespace
     */
    UUID,
    /**
     * Entity label formatted to {@literal CamelCase} and appended to the namespace
     */
    LABEL_CAMEL_CASE,
    /**
     * Entity label formatted to {@literal snake_case} and appended to the namespace
     */
    LABEL_SNAKE_CASE,
    /**
     * Entity label formatted to {@literal kebab-case} and appended to the namespace
     */
    LABEL_KEBAB_CASE
}
