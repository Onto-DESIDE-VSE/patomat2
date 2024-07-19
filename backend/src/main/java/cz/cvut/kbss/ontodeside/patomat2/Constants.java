package cz.cvut.kbss.ontodeside.patomat2;

public class Constants {

    /**
     * HTTP session attribute name for stored ontology file.
     */
    public static final String ONTOLOGY_FILE_SESSION_ATTRIBUTE = "ontologyFile";

    /**
     * HTTP session attribute name for stored patterns.
     */
    public static final String PATTERN_FILES_SESSION_ATTRIBUTE = "patternFiles";

    /**
     * Session timeout in milliseconds.
     */
    public static final int SESSION_TIMEOUT = 30 * 60 * 1000;

    private Constants() {
        throw new AssertionError();
    }
}
