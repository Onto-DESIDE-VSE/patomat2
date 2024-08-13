package cz.vse.swoe.ontodeside.patomat2.util;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

/**
 * NLP pipeline provider.
 */
public class NLPPipelineProvider {

    private static StanfordCoreNLP nlpPipeline;

    public static void initNLPPipeline() {
        if (nlpPipeline != null) {
            return;
        }
        // set up pipeline properties
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,pos,lemma,ner,parse,depparse,coref,kbp,quote");
        // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
        props.setProperty("coref.algorithm", "neural");
        // build pipeline
        nlpPipeline = new StanfordCoreNLP(props);
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
            initNLPPipeline();
        }
        return nlpPipeline;
    }

    private NLPPipelineProvider() {
        throw new AssertionError();
    }

}
