package cz.cvut.kbss.ontodeside.patomat2.service;

import cz.cvut.kbss.ontodeside.patomat2.exception.OntologyNotUploadedException;
import cz.cvut.kbss.ontodeside.patomat2.model.NewEntity;
import cz.cvut.kbss.ontodeside.patomat2.model.OntologyDiff;
import cz.cvut.kbss.ontodeside.patomat2.model.PatternInstance;
import cz.cvut.kbss.ontodeside.patomat2.model.PatternInstanceTransformation;
import cz.cvut.kbss.ontodeside.patomat2.model.TransformationSpecification;
import cz.cvut.kbss.ontodeside.patomat2.model.TransformationSummary;
import cz.cvut.kbss.ontodeside.patomat2.util.FileAwareByteArrayResource;
import cz.cvut.kbss.ontodeside.patomat2.util.Utils;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TransformationService {

    private final MatchService matchService;

    private final OntologyHolder ontologyHolder;

    private final OntologyStoringService storingService;

    private final TransformedOntologyHolder transformedOntologyHolder;

    public TransformationService(MatchService matchService, OntologyHolder ontologyHolder,
                                 OntologyStoringService storingService,
                                 TransformedOntologyHolder transformedOntologyHolder) {
        this.matchService = matchService;
        this.ontologyHolder = ontologyHolder;
        this.storingService = storingService;
        this.transformedOntologyHolder = transformedOntologyHolder;
    }

    /**
     * Executes the pattern-based transformation.
     *
     * @param transformation Transformation specification
     * @return Transformed ontology
     */
    public TransformationSummary transform(@NonNull TransformationSpecification transformation) {
        Objects.requireNonNull(transformation);
        if (!ontologyHolder.isLoaded()) {
            throw new OntologyNotUploadedException("Ontology has not been uploaded yet.");
        }
        try {
            final Map<Integer, PatternInstance> matches = matchService.getMatches();
            final List<PatternInstance> appliedInstances = new ArrayList<>();
            transformation.getPatternInstances().forEach(pit -> {
                final PatternInstance instance = matches.get(pit.getId()).deepCopy();
                replaceGeneratedLabelsWithUserSpecified(instance, pit);
                applyTransformation(instance, transformation.isApplyDeletes());
                appliedInstances.add(instance);
            });
            final String ontologyFilename = storingService.getUploadedOntologyFileName().orElse(null);
            final Resource newOntology = new FileAwareByteArrayResource(ontologyHolder.export(Utils.filenameToMimeType(ontologyFilename))
                                                                                      .toByteArray(), ontologyFilename);
            transformedOntologyHolder.setTransformedOntology(newOntology);
            final OntologyDiff diff = ontologyHolder.difference(storingService.getOntologyFile());
            return new TransformationSummary(diff.addedStatements(), diff.removedStatements(),
                    appliedInstances.stream().map(pi -> pi.newEntities().size()).reduce(0, Integer::sum));
        } catch (RuntimeException e) {
            matchService.clear();
            throw e;
        } finally {
            // Reload the original untransformed ontology
            ontologyHolder.clear();
            ontologyHolder.loadOntology(storingService.getOntologyFile());
        }
    }

    private void applyTransformation(PatternInstance instance, boolean applyDeletes) {
        if (applyDeletes) {
            ontologyHolder.applyTransformationQuery(instance.sparqlDelete());
        }
        ontologyHolder.applyTransformationQuery(instance.sparqlInsert());
        instance.newEntities().forEach(ne -> ontologyHolder.applyTransformationQuery(ne.createInsertLabelSparql()));
    }

    private static void replaceGeneratedLabelsWithUserSpecified(PatternInstance instance,
                                                                PatternInstanceTransformation pit) {
        if (pit.getNewEntityLabels() == null) {
            return;
        }
        pit.getNewEntityLabels().forEach((var, labels) -> {
            for (int i = 0; i < instance.newEntities().size(); i++) {
                final NewEntity ne = instance.newEntities().get(i);
                if (ne.variableName().equals(var)) {
                    instance.newEntities().set(i, new NewEntity(ne.variableName(), ne.identifier(), labels));
                }
            }
        });
    }

    /**
     * Gets the last transformed ontology.
     *
     * @return The last transformed ontology version
     * @throws IllegalStateException if transformation has not been applied
     */
    public Resource getTransformedOntology() {
        if (transformedOntologyHolder.isEmpty()) {
            throw new IllegalStateException("Ontology transformation has not been applied yet.");
        }
        return transformedOntologyHolder.getTransformedOntology();
    }
}
