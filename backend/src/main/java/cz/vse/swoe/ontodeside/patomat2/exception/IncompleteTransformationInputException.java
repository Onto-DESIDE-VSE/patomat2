package cz.vse.swoe.ontodeside.patomat2.exception;

/**
 * Indicates that the transformation input is incomplete.
 * <p>
 * This typically means that the user requested an operation with the transformation input but has not provided the
 * input (the ontology or the patterns).
 */
public class IncompleteTransformationInputException extends PatOMat2Exception {

    public IncompleteTransformationInputException(String message) {
        super(message);
    }
}
