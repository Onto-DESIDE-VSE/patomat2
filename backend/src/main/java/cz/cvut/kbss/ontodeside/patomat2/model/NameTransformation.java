package cz.cvut.kbss.ontodeside.patomat2.model;

import cz.cvut.kbss.ontodeside.patomat2.Constants;
import cz.cvut.kbss.ontodeside.patomat2.exception.NameTransformationException;
import cz.cvut.kbss.ontodeside.patomat2.service.OntologyHolder;
import cz.cvut.kbss.ontodeside.patomat2.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Describes how a new entity name should be created based the transformation rule.
 *
 * @param variableName Name of the variable representing a new entity
 * @param rule         Rule for name generation
 */
public record NameTransformation(String variableName, String rule) {

    /**
     * Generates a new entity name based on the transformation rule using the provided data.
     * <p>
     * Since the rule may reference other variables in the pattern, the specified pattern match and ontology are used to
     * retrieve relevant values (variable values, their labels).
     *
     * @param match          Pattern instance
     * @param ontologyHolder Ontology holder
     * @return New entity name based on the transformation rule
     */
    public String generateName(PatternMatch match, OntologyHolder ontologyHolder) {
        final Set<String> variables = StringUtil.extractSparqlVariables(rule);
        final Map<String, String> variableLabels = new HashMap<>(variables.size());
        variables.forEach(v -> {
            final String value = match.getBinding(v).map(ResultBinding::value)
                                      .orElseThrow(() -> new NameTransformationException("No value for name transformation rule variable " + Constants.SPARQL_VARIABLE + v));
            variableLabels.put(v, ontologyHolder.getLabel(value).orElseGet(() -> extractLabelFromIdentifier(value)));
        });
        String label = rule;
        for (Map.Entry<String, String> entry : variableLabels.entrySet()) {
            label = label.replace(Constants.SPARQL_VARIABLE + entry.getKey(), entry.getValue());
        }
        return label;
    }

    private static String extractLabelFromIdentifier(String id) {
        final String localPart = id.contains("#") ? id.substring(id.lastIndexOf('#') + 1) : id.substring(id.lastIndexOf('/') + 1);
        String label = localPart.replace('-', ' ');
        label = label.replace('_', ' ');
        final String[] parts = label.split(" ");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
        }
        return String.join(" ", parts);
    }
}
