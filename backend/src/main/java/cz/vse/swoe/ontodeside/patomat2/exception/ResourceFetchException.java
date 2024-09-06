package cz.vse.swoe.ontodeside.patomat2.exception;

/**
 * Indicates that a resource cannot be fetched over the internet.
 */
public class ResourceFetchException extends PatOMat2Exception {

    public ResourceFetchException(String message) {
        super(message);
    }
}
