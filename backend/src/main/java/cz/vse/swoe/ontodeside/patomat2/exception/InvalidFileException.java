package cz.vse.swoe.ontodeside.patomat2.exception;

/**
 * Indicates that an uploaded file is invalid.
 */
public class InvalidFileException extends PatOMat2Exception {

    public InvalidFileException(String message) {
        super(message);
    }
}
