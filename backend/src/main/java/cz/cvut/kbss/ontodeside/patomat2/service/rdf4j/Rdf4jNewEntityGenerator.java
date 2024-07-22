package cz.cvut.kbss.ontodeside.patomat2.service.rdf4j;

import cz.cvut.kbss.ontodeside.patomat2.model.NewEntityGenerator;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class Rdf4jNewEntityGenerator implements NewEntityGenerator {

    private final Rdf4jOntologyHolder ontologyHolder;

    public Rdf4jNewEntityGenerator(Rdf4jOntologyHolder ontologyHolder) {
        this.ontologyHolder = ontologyHolder;
    }

    @Override
    public URI generateIdentifier() {
        return null;
    }
}
