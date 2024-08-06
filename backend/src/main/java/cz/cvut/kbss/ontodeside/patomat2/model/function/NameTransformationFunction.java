package cz.cvut.kbss.ontodeside.patomat2.model.function;

import cz.cvut.kbss.ontodeside.patomat2.model.PatternMatch;
import cz.cvut.kbss.ontodeside.patomat2.service.OntologyHolder;

public abstract class NameTransformationFunction {

    protected final OntologyHolder ontologyHolder;

    private final NameTransformationFunction next;

    protected NameTransformationFunction(OntologyHolder ontologyHolder, NameTransformationFunction next) {
        this.ontologyHolder = ontologyHolder;
        this.next = next;
    }

    public String apply(PatternMatch match, String nameTransformationRule) {
        if (next != null) {
            return next.apply(match, applyInternal(match, nameTransformationRule));
        }
        return applyInternal(match, nameTransformationRule);
    }

     abstract String applyInternal(PatternMatch match, String nameTransformationRule);
}
