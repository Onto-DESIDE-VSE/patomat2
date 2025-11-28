package cz.vse.swoe.ontodeside.patomat2.model.iri;

import cz.vse.swoe.ontodeside.patomat2.model.EntityLabel;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Generates local name for new IRI.
 */
public class EntityIriLocalNameGenerator {

    private static final Random RAND = new Random();

    /**
     * Generates local name for an entity IRI, based on the specified format.
     *
     * @param format Format of the local name
     * @param labels Entity labels for cases where the local name is based on the label
     * @return Generated local name
     */
    public static String generateLocalName(EntityIriLocalNameFormat format, List<EntityLabel> labels) {
        return switch (format) {
            case RANDOM_NUMBER -> String.valueOf(RAND.nextInt(100000));
            case UUID -> UUID.randomUUID().toString();
            case LABEL_PASCAL_CASE -> {
                assert !labels.isEmpty();
                final String value = labels.getFirst().value();
                final String[] words = value.split(" ");
                final StringBuilder sb = new StringBuilder();
                for (String word : words) {
                    sb.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
                }
                yield sb.toString();
            }
            case LABEL_CAMEL_CASE -> {
                assert !labels.isEmpty();
                final String value = labels.getFirst().value();
                final String[] words = value.split(" ");
                final StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (String word : words) {
                    sb.append(first ? word.substring(0, 1).toLowerCase() : word.substring(0, 1).toUpperCase())
                      .append(word.substring(1).toLowerCase());
                    first = false;
                }
                yield sb.toString();
            }
            case LABEL_SNAKE_CASE -> {
                assert !labels.isEmpty();
                yield labels.getFirst().value().toLowerCase().replaceAll(" ", "_");
            }
            case LABEL_KEBAB_CASE -> {
                assert !labels.isEmpty();
                yield labels.getFirst().value().toLowerCase().replaceAll(" ", "-");
            }
        };
    }
}
