package cz.vse.swoe.ontodeside.patomat2.model;

/**
 * Representation of an entity label.
 *
 * @param value    Value of the label
 * @param property Property to represent the label, e.g., rdfs:label, skos:prefLabel
 */
public record EntityLabel(String value, String property) {
}
