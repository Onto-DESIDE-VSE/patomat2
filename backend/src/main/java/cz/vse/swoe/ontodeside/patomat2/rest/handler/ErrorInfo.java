package cz.vse.swoe.ontodeside.patomat2.rest.handler;

/**
 * Contains information about an error and can be sent to client as JSON to let him know what is wrong.
 */
public class ErrorInfo {

    private String requestUri;

    private String message;

    public ErrorInfo(String requestUri, String message) {
        this.requestUri = requestUri;
        this.message = message;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Creates a new instance with the specified message and request URI.
     *
     * @param requestUri URI of the request which caused the error
     * @param message    Error message
     * @return New {@code ErrorInfo} instance
     */
    public static ErrorInfo createWithMessage(String requestUri, String message) {
        return new ErrorInfo(requestUri, message);
    }
}
