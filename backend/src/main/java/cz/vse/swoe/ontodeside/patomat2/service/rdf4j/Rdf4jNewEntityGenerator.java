package cz.vse.swoe.ontodeside.patomat2.service.rdf4j;

import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.config.ApplicationConfig;
import cz.vse.swoe.ontodeside.patomat2.model.EntityLabel;
import cz.vse.swoe.ontodeside.patomat2.model.NameTransformation;
import cz.vse.swoe.ontodeside.patomat2.model.NewEntity;
import cz.vse.swoe.ontodeside.patomat2.model.NewEntityGenerator;
import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
import cz.vse.swoe.ontodeside.patomat2.model.iri.EntityIriLocalNameFormat;
import cz.vse.swoe.ontodeside.patomat2.model.iri.EntityIriLocalNameGenerator;
import cz.vse.swoe.ontodeside.patomat2.model.iri.NewEntityIriConfig;
import cz.vse.swoe.ontodeside.patomat2.service.OntologyHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates new entities (RDF resources) using the loaded ontology.
 */
@SessionScope
@Component
public class Rdf4jNewEntityGenerator implements NewEntityGenerator {

    private final OntologyHolder ontologyHolder;

    private final ApplicationConfig config;

    private NewEntityIriConfig iriConfig;

    public Rdf4jNewEntityGenerator(OntologyHolder ontologyHolder, ApplicationConfig config) {
        this.ontologyHolder = ontologyHolder;
        this.config = config;
    }

    @Override
    public String generateIdentifier(List<EntityLabel> labels) {
        assert ontologyHolder.isLoaded();
        String ontologyIri = iriConfig.namespace();
        final char last = ontologyIri.charAt(ontologyIri.length() - 1);
        if (last != '/' && last != '#') {
            ontologyIri += '/';
        }
        return ontologyIri + EntityIriLocalNameGenerator.generateLocalName(iriConfig.localNameFormat(), labels);
    }

    @Override
    public NewEntity generateNewEntity(@NonNull String variableName,
                                       @NonNull List<NameTransformation> nameTransformations,
                                       @NonNull PatternMatch match) {
        final List<EntityLabel> labels = nameTransformations.stream()
                                                            .map(n -> new EntityLabel(n.generateName(match, ontologyHolder), Constants.DEFAULT_LABEL_PROPERTY))
                                                            .collect(Collectors.toList());
        final String id = generateIdentifier(labels);
        return new NewEntity(variableName, id, labels);
    }

    @Override
    public void setIriConfig(NewEntityIriConfig iriConfig) {
        this.iriConfig = iriConfig;
    }

    @Override
    public NewEntityIriConfig getIriConfig() {
        return iriConfig;
    }

    @Override
    public void initNewEntityIriConfig() {
        this.iriConfig = new NewEntityIriConfig(ontologyHolder.getOntologyIri()
                                                              .orElse(config.getNewEntityIriBase()), EntityIriLocalNameFormat.RANDOM_NUMBER);
    }
}
