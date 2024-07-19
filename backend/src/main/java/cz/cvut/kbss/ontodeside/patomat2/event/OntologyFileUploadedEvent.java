package cz.cvut.kbss.ontodeside.patomat2.event;

import org.springframework.context.ApplicationEvent;

/**
 * Indicates that an ontology file has been uploaded.
 */
public class OntologyFileUploadedEvent extends ApplicationEvent {

    private final String ontologyFileName;

    public OntologyFileUploadedEvent(Object source, String ontologyFileName) {
        super(source);
        this.ontologyFileName = ontologyFileName;
    }

    public String getOntologyFileName() {
        return ontologyFileName;
    }
}
