package cz.vse.swoe.ontodeside.patomat2.model.function;

import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
import cz.vse.swoe.ontodeside.patomat2.service.OntologyHolder;
import cz.vse.swoe.ontodeside.patomat2.util.NLPUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.util.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.regex.Pattern;

public class NominalizeFunction extends NameTransformationFunction {

    private static final Logger LOG = LoggerFactory.getLogger(NominalizeFunction.class);

    // Include the variable delimiter in the match group so that we can determine when we are dealing with a variable
    private static final Pattern PATTERN = Pattern.compile("nominalize\\(\\s?(\\??[a-zA-Z0-9\\s]+)\\s?\\)");

    public NominalizeFunction(OntologyHolder ontologyHolder, NameTransformationFunction next) {
        super(ontologyHolder, next);
    }

    @Override
    protected Pattern getPattern() {
        return PATTERN;
    }

    @Override
    protected String applyInternal(PatternMatch match, String argument) {
        final String value = argument.startsWith(Constants.SPARQL_VARIABLE) ? getBindingValue(match, argument.substring(1)) : argument;
        CoreDocument document = new CoreDocument(value);
        NLPUtils.getNLPPipeline().annotate(document);
        final CoreMap coreMap = document.annotation();
        CoreLabel verbLabel = null;
        for (CoreLabel token : coreMap.get(CoreAnnotations.TokensAnnotation.class)) {
            final String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            if (pos.startsWith("VB")) {
                verbLabel = token;
                break;
            }
        }
        if (verbLabel == null) {
            LOG.trace("No verb to nominalize found in value '{}'.", value);
            return getNoun(coreMap).orElse(value);
        }
        return nominalizeVerb(verbLabel.get(CoreAnnotations.LemmaAnnotation.class)).orElseGet(() -> getNoun(coreMap).orElse(value));
    }

    private Optional<String> nominalizeVerb(String verb) {
        if (NLPUtils.getVerbsToNouns().containsKey(verb)) {
            return Optional.of(NLPUtils.getVerbsToNouns().get(verb).getFirst());
        }
        LOG.trace("No suitable noun found for verb '{}'.", verb);
        return Optional.empty();
    }

    private Optional<String> getNoun(CoreMap tokens) {
        for (CoreLabel token : tokens.get(CoreAnnotations.TokensAnnotation.class)) {
            final String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            if (pos.startsWith("NN")) {
                return Optional.of(token.get(CoreAnnotations.LemmaAnnotation.class));
            }
        }
        return Optional.empty();
    }
}
