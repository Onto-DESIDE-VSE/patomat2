package cz.vse.swoe.ontodeside.patomat2.model.function;

import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
import cz.vse.swoe.ontodeside.patomat2.service.OntologyHolder;
import cz.vse.swoe.ontodeside.patomat2.util.NLPUtils;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Extracts head noun from the specified argument.
 */
public class HeadNounFunction extends NameTransformationFunction {

    private static final Logger LOG = LoggerFactory.getLogger(HeadNounFunction.class);

    // Include the variable delimiter in the match group so that we can determine when we are dealing with a variable
    private static final Pattern PATTERN = Pattern.compile("head_noun\\(\\s?(\\??[a-zA-Z0-9\\s]+)\\s?\\)");

    public HeadNounFunction(OntologyHolder ontologyHolder, NameTransformationFunction next) {
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
        HeadFinder hf = new PennTreebankLanguagePack().headFinder();

        final Tree myTree = document.sentences().getFirst().constituencyParse();
        final TregexPattern tPattern = TregexPattern.compile("NP");
        final TregexMatcher tMatcher = tPattern.matcher(myTree);
        if (tMatcher.find()) {
            final Tree nounPhrase = tMatcher.getMatch();
            final Tree headConstituent = hf.determineHead(nounPhrase);
            if (headConstituent.value().startsWith("NN")) {
                return headConstituent.getChildrenAsList().getFirst().label().value();
            }
            final Optional<Tree> head = headConstituent.getChildrenAsList().stream()
                                                       .filter(t -> t.value().startsWith("NN")).findFirst();
            if (head.isPresent()) {
                return head.get().getChildrenAsList().getFirst().label().value();
            }
        }
        LOG.warn("Unable to determine head noun of value '{}'.", value);
        return value;
    }
}
