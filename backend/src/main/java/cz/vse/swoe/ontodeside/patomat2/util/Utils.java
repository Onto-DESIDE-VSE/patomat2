package cz.vse.swoe.ontodeside.patomat2.util;

import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.exception.PatOMat2Exception;
import org.eclipse.rdf4j.common.lang.FileFormat;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;

public class Utils {

    private Utils() {
        throw new AssertionError();
    }

    /**
     * Extracts SPARQL variables from the specified string.
     * <p>
     * The string may be a triple pattern, name transformation rule or a part of a SPARQL query.
     *
     * @param str String to extract variables from
     * @return Set of variable names found in the string
     */
    public static Set<String> extractSparqlVariables(String str) {
        final Matcher matcher = Constants.SPARQL_VARIABLE_PATTERN.matcher(str);
        final Set<String> vars = new HashSet<>();
        while (matcher.find()) {
            vars.add(matcher.group(1));
        }
        return vars;
    }

    /**
     * Determines MIME type from the specified file name.
     *
     * @param filename File name
     * @return Resolved MIME type
     * @throws PatOMat2Exception If unable to determine MIME type
     */
    public static String filenameToMimeType(String filename) {
        final Optional<RDFFormat> mimeType = Rio.getParserFormatForFileName(filename);
        return mimeType.map(FileFormat::getDefaultMIMEType)
                       .orElseThrow(() -> new PatOMat2Exception("Unable to determine MIME type for file '" + filename + "'."));
    }

    /**
     * Creates an entity label from its specified identifier.
     *
     * @param id Entity identifier (IRI)
     * @return Extracted label
     */
    public static String createLabelFromIdentifier(String id) {
        Objects.requireNonNull(id);
        final String localPart = id.contains("#") ? id.substring(id.lastIndexOf('#') + 1) : id.substring(id.lastIndexOf('/') + 1);
        String label = localPart.replace('-', ' ');
        label = label.replace('_', ' ');
        label = splitCamelCaseStringToWords(label);
        final String[] parts = label.split(" ");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
        }
        return String.join(" ", parts);
    }

    private static String splitCamelCaseStringToWords(String str) {
        String[] words = str.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].trim();
        }
        return String.join(" ", words);
    }
}
