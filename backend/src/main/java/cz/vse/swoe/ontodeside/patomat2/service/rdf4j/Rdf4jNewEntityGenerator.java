package cz.vse.swoe.ontodeside.patomat2.service.rdf4j;

import cz.vse.swoe.ontodeside.patomat2.config.ApplicationConfig;
import cz.vse.swoe.ontodeside.patomat2.model.NameTransformation;
import cz.vse.swoe.ontodeside.patomat2.model.NewEntity;
import cz.vse.swoe.ontodeside.patomat2.model.NewEntityGenerator;
import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
import cz.vse.swoe.ontodeside.patomat2.service.OntologyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
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
    public String generateIdentifier() {
        assert ontologyHolder.isLoaded();
        String ontologyIri = ontologyHolder.getOntologyIri().orElse(config.getNewEntityIriBase());
        final char last = ontologyIri.charAt(ontologyIri.length() - 1);
        if (last != '/' && last != '#') {
            ontologyIri += '/';
        }
        return ontologyIri + RAND.nextInt(100000);
    }

    @Override
    public NewEntity generateNewEntity(@NonNull String variableName,
                                       @NonNull List<NameTransformation> nameTransformations,
                                       @NonNull PatternMatch match) {
        final String id = generateIdentifier();
        return new NewEntity(variableName, id, nameTransformations.stream()
                                                                  .map(n -> n.generateName(match, ontologyHolder))
                                                                  .toList());
    }
}
