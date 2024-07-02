package cz.cvut.kbss.ontodeside.patomat2.exception;

/**
 * Indicates an illegal state of the application - the user requested an operation with the ontology but has not
 * uploaded it yet.
 */
public class OntologyNotUploadedException extends PatOMat2Exception {

    public OntologyNotUploadedException(String message) {
        super(message);
    }
}
