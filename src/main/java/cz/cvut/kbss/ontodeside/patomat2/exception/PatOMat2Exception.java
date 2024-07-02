package cz.cvut.kbss.ontodeside.patomat2.exception;

/**
 * Base exception for PatOMat2.
 */
public class PatOMat2Exception extends RuntimeException {

    public PatOMat2Exception(String message) {
        super(message);
    }

    public PatOMat2Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public PatOMat2Exception(Throwable cause) {
        super(cause);
    }
}
