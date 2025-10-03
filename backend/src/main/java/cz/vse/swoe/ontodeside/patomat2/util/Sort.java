package cz.vse.swoe.ontodeside.patomat2.util;

/**
 * Options for sorting pattern matches.
 */
public enum Sort {

    /**
     * Random shuffle of pattern matches.
     */
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
}
