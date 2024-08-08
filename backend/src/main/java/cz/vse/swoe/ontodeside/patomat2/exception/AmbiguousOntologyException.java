package cz.vse.swoe.ontodeside.patomat2.exception;

/**
 * Indicates that we are unable to determine which ontology to use for an operation.
 */
public class AmbiguousOntologyException extends PatOMat2Exception{

    public AmbiguousOntologyException(String message) {
        super(message);
    }
}
