package cz.vse.swoe.ontodeside.patomat2.model.function;

import cz.vse.swoe.ontodeside.patomat2.exception.NameTransformationException;
import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
import cz.vse.swoe.ontodeside.patomat2.model.ResultBinding;
import cz.vse.swoe.ontodeside.patomat2.service.OntologyHolder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class NameTransformationFunction {

    protected final OntologyHolder ontologyHolder;

    private final NameTransformationFunction next;

    protected NameTransformationFunction(OntologyHolder ontologyHolder, NameTransformationFunction next) {
        this.ontologyHolder = ontologyHolder;
        this.next = next;
    }

    /**
     * Applies this function to the specified name transformation rule using the specified pattern match as data
     * context.
     * <p>
     * If this function has a next function, it is applied as well until the whole chain is exhausted.
     *
     * @param match                  Data providing context
     * @param nameTransformationRule Rule to use when transforming the name
     * @return Name transformation rule with applied functions
     */
    public String apply(PatternMatch match, String nameTransformationRule) {
        String result = nameTransformationRule;
        final Matcher matcher = getPattern().matcher(nameTransformationRule);
        while (matcher.find()) {
            final String instance = matcher.group(0);
            final String argument = matcher.group(1);
            String singleApplicationResult = applyInternal(match, argument);
            result = result.replace(instance, singleApplicationResult);
        }
        return next != null ? next.apply(match, result) : result;
    }

    protected ResultBinding getBinding(PatternMatch match, String name) {
        return match.getBinding(name)
                    .orElseThrow(() -> new NameTransformationException("Variable '" + name + "' not found in pattern instance."));
    }

    protected abstract Pattern getPattern();

    protected abstract String applyInternal(PatternMatch match, String argument);
}
