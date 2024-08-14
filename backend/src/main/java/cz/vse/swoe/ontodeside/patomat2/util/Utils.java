package cz.vse.swoe.ontodeside.patomat2.util;

import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.exception.PatOMat2Exception;
import org.eclipse.rdf4j.common.lang.FileFormat;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.stream.Stream;

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
        final String localPart = extractLocalPart(id);
        final String[] parts = tokenize(localPart);
        capitalize(parts);
        return String.join(" ", parts);
    }

    /**
     * Extracts local part of the specified URI/IRI identifier.
     * <p>
     * The local part is either part after the hash or the part after the last slash.
     *
     * @param id Identifier whose local part to extract
     * @return Extracted local part
     */
    public static String extractLocalPart(String id) {
        Objects.requireNonNull(id);
        return id.contains("#") ? id.substring(id.lastIndexOf('#') + 1) : id.substring(id.lastIndexOf('/') + 1);
    }

    /**
     * Tokenizes the specified string.
     * <p>
     * It splits it by underscore, dash, whitespace and camel case words.
     *
     * @param str String to tokenize
     * @return Array of tokens
     */
    public static String[] tokenize(String str) {
        Objects.requireNonNull(str);
        String[] tokens = str.split("[\\s-_]");
        return Stream.of(tokens).flatMap(s -> Stream.of(s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")))
                     .toArray(String[]::new);
    }

    /**
     * Capitalizes the specified tokens in place.
     * <p>
     * This means making the first letter of each token uppercase.
     *
     * @param tokens Tokens to capitalize
     */
    public static void capitalize(String[] tokens) {
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].substring(0, 1).toUpperCase() + tokens[i].substring(1);
        }
    }

    /**
     * Reads a resource from the classpath and applies the specified consumer to each line.
     *
     * @param name     Name (path) of the classpath resource
     * @param consumer Consumer to apply to each line
     */
    public static void readClasspathResource(String name, Consumer<String> consumer) {
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(Utils.class.getClassLoader()
                                                                                           .getResourceAsStream(name)))) {
            String line;
            while ((line = in.readLine()) != null) {
                consumer.accept(line);
            }
        } catch (IOException e) {
            throw new PatOMat2Exception("Unable to read classpath resource.", e);
        }
    }
}
