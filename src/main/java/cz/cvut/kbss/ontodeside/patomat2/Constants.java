package cz.cvut.kbss.ontodeside.patomat2;

public class Constants {

    /**
     * HTTP session attribute name for stored ontology file.
     */
    public static final String ONTOLOGY_FILE_ATTRIBUTE = "ontologyFile";

    /**
     * HTTP session attribute name for stored pattern files.
     */
    public static final String PATTERN_FILES_ATTRIBUTE = "patternFiles";

    private Constants() {
        throw new AssertionError();
    }
}
