package cz.cvut.kbss.ontodeside.patomat2.service.rdf4j;

import cz.cvut.kbss.ontodeside.patomat2.config.ApplicationConfig;
import cz.cvut.kbss.ontodeside.patomat2.model.NewEntityGenerator;
import cz.cvut.kbss.ontodeside.patomat2.service.OntologyHolder;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Random;

/**
 * Generates new entity (RDF resource) identifier using the loaded ontology.
 */
@Component
public class Rdf4jNewEntityGenerator implements NewEntityGenerator {

    private static final Random RAND = new Random();

    private final OntologyHolder ontologyHolder;

    private final ApplicationConfig config;

    public Rdf4jNewEntityGenerator(OntologyHolder ontologyHolder, ApplicationConfig config) {
        this.ontologyHolder = ontologyHolder;
        this.config = config;
    }

    @Override
    public URI generateIdentifier() {
        assert ontologyHolder.isLoaded();
        String ontologyIri = ontologyHolder.getOntologyIri().orElse(config.getNewEntityIriBase());
        final char last = ontologyIri.charAt(ontologyIri.length() - 1);
        if (last != '/' && last != '#') {
            ontologyIri += '/';
        }
        return URI.create(ontologyIri + RAND.nextInt(100000));
    }
}
