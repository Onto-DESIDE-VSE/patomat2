package cz.vse.swoe.ontodeside.patomat2.util;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * NLP pipeline provider.
 */
public class NLPUtils {

    private static StanfordCoreNLP nlpPipeline;

    // Based on the WordNet morphosemantic links database
    private static Map<String, List<String>> verbsToNouns;
    private static Map<String, List<String>> nounsToVerbs;

    public static void init() {
        if (nlpPipeline != null) {
            return;
        }
        nlpPipeline = initNlpPipeline();
        loadMorphosemanticLinks();
    }

    private static StanfordCoreNLP initNlpPipeline() {
        // set up pipeline properties
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,pos,lemma,ner,parse,depparse,coref,kbp,quote");
        // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
        props.setProperty("coref.algorithm", "neural");
        // build pipeline
        return new StanfordCoreNLP(props);
    }

    private static void loadMorphosemanticLinks() {
        verbsToNouns = new LinkedHashMap<>();
        nounsToVerbs = new LinkedHashMap<>();
        Utils.readClasspathResource("morphosemantic-links.csv", line -> {
            // We disregard most of the data here, just extract the verb and noun
            final String[] parts = line.split(",");
            final String verb = parts[0].substring(0, parts[0].indexOf('%'));
            final String noun = parts[3].substring(0, parts[3].indexOf('%'));
            verbsToNouns.computeIfAbsent(verb, k -> new ArrayList<>()).add(noun);
            nounsToVerbs.computeIfAbsent(noun, k -> new ArrayList<>()).add(verb);
        });
    }

    /**
     * Gets a NLP pipeline.
     *
     * @return Stanford Core NLP pipeline
     */
    public static StanfordCoreNLP getNLPPipeline() {
        if (nlpPipeline == null) {
            // This is not lazy loading. It is expected that the pipeline will be initialized at application startup,
            // because the initialization is quite time-consuming and running it when it is needed is not good for performance.
            // This if is a fallback for cases when we forgot to initialize the pipeline.
            init();
        }
        return nlpPipeline;
    }

    public static Map<String, List<String>> getVerbsToNouns() {
        if (verbsToNouns == null) {
            init();
        }
        return Collections.unmodifiableMap(verbsToNouns);
    }

    public static Map<String, List<String>> getNounsToVerbs() {
        if (nounsToVerbs == null) {
            init();
        }
        return Collections.unmodifiableMap(nounsToVerbs);
    }

    private NLPUtils() {
        throw new AssertionError();
    }
}
