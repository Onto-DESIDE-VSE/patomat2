package cz.vse.swoe.ontodeside.patomat2.exception;

/**
 * Indicates a blank node in pattern match query result.
 */
public class BlankNodeResultException extends PatOMat2Exception {
    public BlankNodeResultException(String message) {
        super(message);
    }
}
