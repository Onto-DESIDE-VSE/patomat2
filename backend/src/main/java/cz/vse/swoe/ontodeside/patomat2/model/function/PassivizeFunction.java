package cz.vse.swoe.ontodeside.patomat2.model.function;

import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
import cz.vse.swoe.ontodeside.patomat2.service.OntologyHolder;
import cz.vse.swoe.ontodeside.patomat2.util.NLPUtils;
import cz.vse.swoe.ontodeside.patomat2.util.Utils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.util.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Finds a verb in the specified argument and returns it in a passive form.
 */
public class PassivizeFunction extends NameTransformationFunction {

    private static final Logger LOG = LoggerFactory.getLogger(PassivizeFunction.class);

    // Include the variable delimiter in the match group so that we can determine when we are dealing with a variable
    private static final Pattern PATTERN = Pattern.compile("passivize\\(\\s?(\\??[a-zA-Z0-9\\s]+)\\s?\\)");

    // Dictionary of English irregular verbs together with their past participle form
    private static final Map<String, String> IRREGULAR_VERBS = loadIrregularVerbs();

    public PassivizeFunction(OntologyHolder ontologyHolder, NameTransformationFunction next) {
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
        for (CoreLabel token : coreMap.get(CoreAnnotations.TokensAnnotation.class)) {
            final String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            if (pos.equals("VBN")) {
                return token.word();
            }
            if (pos.startsWith("VB")) {
                return passivizeVerb(token.get(CoreAnnotations.LemmaAnnotation.class));
            }
        }
        LOG.trace("No verb to passivize found in value '{}', trying to use a noun.", value);
        return findNounAndMakeVerb(coreMap).map(this::passivizeVerb).orElse(value);
    }

    private String passivizeVerb(String lemma) {
        if (IRREGULAR_VERBS.containsKey(lemma)) {
            return IRREGULAR_VERBS.get(lemma);
        }
        if (lemma.endsWith("y")) {
            return lemma.substring(0, lemma.length() - 1) + "ied";
        } else if (lemma.endsWith("e")) {
            return lemma + "d";
        }
        return lemma + "ed";
    }

    private Optional<String> findNounAndMakeVerb(CoreMap coreMap) {
        for (CoreLabel token : coreMap.get(CoreAnnotations.TokensAnnotation.class)) {
            final String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            if (pos.startsWith("NN")) {
                return nounToVerb(token.get(CoreAnnotations.LemmaAnnotation.class));
            }
        }
        LOG.trace("No suitable noun found for passivization.");
        return Optional.empty();
    }

    private Optional<String> nounToVerb(String noun) {
        if (NLPUtils.getNounsToVerbs().containsKey(noun)) {
            return Optional.of(NLPUtils.getNounsToVerbs().get(noun).getFirst());
        }
        LOG.trace("No suitable verb found for noun '{}'.", noun);
        return Optional.empty();
    }

    private static Map<String, String> loadIrregularVerbs() {
        LOG.trace("Loading the dictionary of irregular verbs.");
        final Map<String, String> result = new HashMap<>();
        Utils.readClasspathResource("irregular-verbs.csv", line -> {
            final String[] parts = line.split(",");
            assert parts.length == 3;
            result.put(parts[0], parts[2]);
        });
        return result;
    }
}
