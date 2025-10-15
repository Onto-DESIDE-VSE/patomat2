package cz.vse.swoe.ontodeside.patomat2.exception;

/**
 * Indicates that the LLM-based sorting failed.
 * <p>
 * Application log should contain details this is just for client notification that sorting failed.
 */
public class LlmSortException extends PatOMat2Exception {

    public LlmSortException(String message) {
        super(message);
    }
}
