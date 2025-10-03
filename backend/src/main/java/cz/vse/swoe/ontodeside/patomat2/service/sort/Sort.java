package cz.vse.swoe.ontodeside.patomat2.service.sort;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Options for sorting pattern matches.
 */
public enum Sort {

    /**
     * Random shuffle of pattern matches.
     */
    @Schema(description = "Random shuffle of pattern matches")
    RANDOM("random", "Random");

    private final String value;
    private final String name;

    Sort(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static Sort fromValue(String value) {
        for (Sort s : values()) {
            if (s.value.equalsIgnoreCase(value)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown sort method " + value);
    }
}
