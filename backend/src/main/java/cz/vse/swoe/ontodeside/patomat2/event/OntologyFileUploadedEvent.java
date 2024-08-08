package cz.vse.swoe.ontodeside.patomat2.event;

import cz.vse.swoe.ontodeside.patomat2.model.Pattern;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * Indicates that an ontology file has been uploaded.
 */
public class OntologyFileUploadedEvent extends ApplicationEvent {

    private final String ontologyFileName;

    private final List<Pattern> patterns;

    public OntologyFileUploadedEvent(Object source, String ontologyFileName, List<Pattern> patterns) {
        super(source);
        this.ontologyFileName = ontologyFileName;
        this.patterns = patterns;
    }

    public String getOntologyFileName() {
        return ontologyFileName;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }
}
