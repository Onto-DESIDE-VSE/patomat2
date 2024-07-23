package cz.cvut.kbss.ontodeside.patomat2.service;

import cz.cvut.kbss.ontodeside.patomat2.exception.OntologyNotUploadedException;
import cz.cvut.kbss.ontodeside.patomat2.model.NewEntity;
import cz.cvut.kbss.ontodeside.patomat2.model.PatternInstance;
import cz.cvut.kbss.ontodeside.patomat2.model.PatternInstanceTransformation;
import cz.cvut.kbss.ontodeside.patomat2.model.TransformationSpecification;
import cz.cvut.kbss.ontodeside.patomat2.util.FileAwareByteArrayResource;
import cz.cvut.kbss.ontodeside.patomat2.util.Utils;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class TransformationService {

    private final MatchService matchService;

    private final OntologyHolder ontologyHolder;

    private final OntologyStoringService storingService;

    public TransformationService(MatchService matchService, OntologyHolder ontologyHolder,
                                 OntologyStoringService storingService) {
        this.matchService = matchService;
        this.ontologyHolder = ontologyHolder;
        this.storingService = storingService;
    }

    /**
     * Executes the pattern-based transformation.
     *
     * @param transformation Transformation specification
     * @return Transformed ontology
     */
    public Resource transform(@NonNull TransformationSpecification transformation) {
        Objects.requireNonNull(transformation);
        if (!ontologyHolder.isLoaded()) {
            throw new OntologyNotUploadedException("Ontology has not been uploaded yet.");
        }
        final Map<Integer, PatternInstance> matches = matchService.getMatches();
        transformation.getPatternInstances().forEach(pit -> {
            final PatternInstance instance = matches.get(pit.getId()).deepCopy();
            replaceGeneratedLabelsWithUserSpecified(instance, pit);
            applyTransformation(instance, transformation.isApplyDeletes());
        });
        final String ontologyFilename = storingService.getUploadedOntologyFileName().orElse(null);
        return new FileAwareByteArrayResource(ontologyHolder.export(Utils.filenameToMimeType(ontologyFilename))
                                                            .toByteArray(), ontologyFilename);
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
        pit.getNewEntityLabels().forEach((var, label) -> {
            for (int i = 0; i < instance.newEntities().size(); i++) {
                final NewEntity ne = instance.newEntities().get(i);
                if (ne.variableName().equals(var)) {
                    instance.newEntities().set(i, new NewEntity(ne.variableName(), ne.identifier(), label));
                }
            }
        });
    }
}
