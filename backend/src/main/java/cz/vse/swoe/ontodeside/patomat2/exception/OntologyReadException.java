package cz.vse.swoe.ontodeside.patomat2.exception;

public class OntologyReadException extends PatOMat2Exception {
    public OntologyReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public OntologyReadException(String message) {
        super(message);
    }
}
