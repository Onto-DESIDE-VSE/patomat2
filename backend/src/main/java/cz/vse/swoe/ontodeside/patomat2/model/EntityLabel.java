package cz.vse.swoe.ontodeside.patomat2.model;

/**
 * Representation of an entity label.
 *
 * @param value    Value of the label
 * @param property Property to represent the label, e.g., rdfs:label, skos:prefLabel
 * @param apply Whether this label should be applied during the transformation
 */
public record EntityLabel(String value, String property, boolean apply) {

    public EntityLabel(String value, String property) {
        this(value, property, true);
    }
}
