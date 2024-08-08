package cz.vse.swoe.ontodeside.patomat2.exception;

/**
 * Indicates that the system was unable to resolve a pattern.
 */
public class PatternParserException extends PatOMat2Exception {

    public PatternParserException(String message) {
        super(message);
    }

    public PatternParserException(String message, Throwable e) {
        super(message, e);
    }
}
