package cz.vse.swoe.ontodeside.patomat2.model.function;

import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.exception.NameTransformationException;
import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
import cz.vse.swoe.ontodeside.patomat2.model.ResultBinding;
import cz.vse.swoe.ontodeside.patomat2.service.OntologyHolder;
import cz.vse.swoe.ontodeside.patomat2.util.Utils;

import java.util.regex.Pattern;

/**
 * Function for resolving label of a resource.
 * <p>
 * It attempts to get the {@code rdfs:label} of the specified argument. If it cannot find it, it extracts the label from
 * the resource identifier by tokenizing and capitalizing its local part.
 */
public class LabelFunction extends NameTransformationFunction {

    private static final Pattern PATTERN = Pattern.compile("label\\(\\s?\\?([a-zA-Z0-9]+)\\s?\\)");

    public LabelFunction(OntologyHolder ontologyHolder, NameTransformationFunction next) {
        super(ontologyHolder, next);
    }

    @Override
    Pattern getPattern() {
        return PATTERN;
    }

    @Override
    String applyInternal(PatternMatch match, String argument) {
        final ResultBinding binding = getBinding(match, argument);
        if (!Constants.RDFS_RESOURCE.equals(binding.datatype())) {
            throw new NameTransformationException("Value of variable '" + argument + "' used in a label function is not a resource.");
        }
        return ontologyHolder.getLabel(binding.value())
                             .orElseGet(() -> Utils.createLabelFromIdentifier(binding.value()));
    }
}
