package cz.cvut.kbss.ontodeside.patomat2.model.function;

import cz.cvut.kbss.ontodeside.patomat2.Constants;
import cz.cvut.kbss.ontodeside.patomat2.exception.NameTransformationException;
import cz.cvut.kbss.ontodeside.patomat2.model.PatternMatch;
import cz.cvut.kbss.ontodeside.patomat2.model.ResultBinding;
import cz.cvut.kbss.ontodeside.patomat2.service.OntologyHolder;
import cz.cvut.kbss.ontodeside.patomat2.util.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LabelFunction extends NameTransformationFunction {

    private static final Pattern PATTERN = Pattern.compile("label\\(\\s?\\?([a-zA-Z0-9]+)\\s?\\)");

    public LabelFunction(OntologyHolder ontologyHolder, NameTransformationFunction next) {
        super(ontologyHolder, next);
    }

    @Override
    String applyInternal(PatternMatch match, String nameTransformationRule) {
        final Matcher matcher = PATTERN.matcher(nameTransformationRule);
        while (matcher.find()) {
            final String instance = matcher.group(0);
            final String variable = matcher.group(1);
            final ResultBinding binding = match.getBinding(variable)
                                               .orElseThrow(() -> new NameTransformationException("Variable '" + variable + "' not found in pattern instance."));
            if (!Constants.RDFS_RESOURCE.equals(binding.datatype())) {
                throw new NameTransformationException("Value of variable '" + variable + "' used in a label function is not a resource.");
            }
            final String label = ontologyHolder.getLabel(binding.value()).orElseGet(() -> Utils.createLabelFromIdentifier(binding.value()));
            nameTransformationRule = nameTransformationRule.replace(instance, label);
        }
        return nameTransformationRule;
    }
}
