package cz.vse.swoe.ontodeside.patomat2.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class TransformedOntologyHolder {

    private Resource transformedOntology;

    public Resource getTransformedOntology() {
        return transformedOntology;
    }

    public void setTransformedOntology(Resource transformedOntology) {
        this.transformedOntology = transformedOntology;
    }

    public boolean isEmpty() {
        return transformedOntology == null;
    }
}
