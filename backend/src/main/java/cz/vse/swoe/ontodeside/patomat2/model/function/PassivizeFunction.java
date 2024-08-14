package cz.vse.swoe.ontodeside.patomat2.model.function;

import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.exception.PatOMat2Exception;
import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
import cz.vse.swoe.ontodeside.patomat2.service.OntologyHolder;
import cz.vse.swoe.ontodeside.patomat2.util.NLPPipelineProvider;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.util.CoreMap;
import net.sf.extjwnl.JWNLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Finds a verb in the specified argument and returns it in a passive form.
 */
public class PassivizeFunction extends NameTransformationFunction {

    private static final Logger LOG = LoggerFactory.getLogger(PassivizeFunction.class);

    // Include the variable delimiter in the match group so that we can determine when we are dealing with a variable
    private static final Pattern PATTERN = Pattern.compile("passivize\\(\\s?(\\??[a-zA-Z0-9\\s]+)\\s?\\)");

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
        NLPPipelineProvider.getNLPPipeline().annotate(document);
        final CoreMap coreMap = document.annotation();
        CoreLabel verbLabel = null;
        for (CoreLabel token : coreMap.get(CoreAnnotations.TokensAnnotation.class)) {
            final String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            if (pos.equals("VBN")) {
                return token.word();
            }
            if (pos.startsWith("VB")) {
                verbLabel = token;
                break;
            }
        }
        if (verbLabel == null) {
            LOG.trace("No verb to passivize found in value '{}'.", value);
            return value;
        }
        try {
            return passivizeVerb(verbLabel);
        } catch (JWNLException e) {
            LOG.warn("Unable to passivize verb in value '{}'.", value, e);
            return value;
        }
    }

    private String passivizeVerb(CoreLabel verb) throws JWNLException {
        final String lemma = verb.get(CoreAnnotations.LemmaAnnotation.class);
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

    private static Map<String, String> loadIrregularVerbs() {
        LOG.trace("Loading the dictionary of irregular verbs.");
        final Map<String, String> result = new HashMap<>();
        final InputStream inputStream = PassivizeFunction.class.getClassLoader().getResourceAsStream("irregular-verbs.csv");
        assert inputStream != null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                final String[] parts = line.split(",");
                assert parts.length == 3;
                result.put(parts[0], parts[2]);
            }
        } catch (IOException e) {
            throw new PatOMat2Exception("Unable to load the dictionary of irregular verbs.", e);
        }
        return result;
    }
}
