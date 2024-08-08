package cz.vse.swoe.ontodeside.patomat2;

import java.util.regex.Pattern;

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
     * Session timeout in seconds.
     */
    public static final int SESSION_TIMEOUT = 60 * 30;

    /**
     * RDFS Resource
     */
    public static final String RDFS_RESOURCE = "http://www.w3.org/2000/01/rdf-schema#Resource";

    /**
     * Prefix of a SPARQL variable
     */
    public static final String SPARQL_VARIABLE = "?";

    /**
     * Regex pattern that extracts SPARQL variable names from strings.
     * <p>
     * That it, it looks for names prefix with {@link #SPARQL_VARIABLE}.
     */
    public static Pattern SPARQL_VARIABLE_PATTERN = Pattern.compile("\\?([a-zA-Z0-9_]+)(?=\\W|$)");

    private Constants() {
        throw new AssertionError();
    }
}
