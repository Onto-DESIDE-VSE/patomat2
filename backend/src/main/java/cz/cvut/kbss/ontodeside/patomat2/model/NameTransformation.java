package cz.cvut.kbss.ontodeside.patomat2.model;

import cz.cvut.kbss.ontodeside.patomat2.Constants;
import cz.cvut.kbss.ontodeside.patomat2.exception.NameTransformationException;
import cz.cvut.kbss.ontodeside.patomat2.model.function.LabelFunction;
import cz.cvut.kbss.ontodeside.patomat2.model.function.NameTransformationFunction;
import cz.cvut.kbss.ontodeside.patomat2.service.OntologyHolder;
import cz.cvut.kbss.ontodeside.patomat2.util.Utils;

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

    public static NameTransformation EMPTY = new NameTransformation("", "");

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
        final NameTransformationFunction functions = transformationFunctions(ontologyHolder);
        String ruleWithAppliedFunctions = functions.apply(match, rule);
        final Set<String> variables = Utils.extractSparqlVariables(ruleWithAppliedFunctions);
        final Map<String, String> variableLabels = new HashMap<>(variables.size());
        variables.forEach(v -> {
            final ResultBinding binding = match.getBinding(v)
                                               .orElseThrow(() -> new NameTransformationException("No value for name transformation rule variable " + Constants.SPARQL_VARIABLE + v));
            String label;
            if (!Constants.RDFS_RESOURCE.equals(binding.datatype())) {
                label = binding.value();
            } else {
                label = Utils.createLabelFromIdentifier(binding.value());
            }
            variableLabels.put(v, label);
        });
        String label = ruleWithAppliedFunctions;
        for (Map.Entry<String, String> entry : variableLabels.entrySet()) {
            label = label.replace(Constants.SPARQL_VARIABLE + entry.getKey(), entry.getValue());
        }
        return label;
    }

    private static NameTransformationFunction transformationFunctions(OntologyHolder ontologyHolder) {
        return new LabelFunction(ontologyHolder, null);
    }
}
