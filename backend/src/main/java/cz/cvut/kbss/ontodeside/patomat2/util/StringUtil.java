package cz.cvut.kbss.ontodeside.patomat2.util;

import cz.cvut.kbss.ontodeside.patomat2.Constants;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

public class StringUtil {

    private StringUtil() {
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
}
