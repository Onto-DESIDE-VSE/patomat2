package cz.vse.swoe.ontodeside.patomat2.model.function;

import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
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

    abstract Pattern getPattern();

    abstract String applyInternal(PatternMatch match, String argument);
}
