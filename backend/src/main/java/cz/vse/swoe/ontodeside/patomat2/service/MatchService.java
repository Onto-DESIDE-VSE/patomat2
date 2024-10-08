package cz.vse.swoe.ontodeside.patomat2.service;

import cz.vse.swoe.ontodeside.patomat2.event.OntologyFileUploadedEvent;
import cz.vse.swoe.ontodeside.patomat2.exception.IncompleteTransformationInputException;
import cz.vse.swoe.ontodeside.patomat2.exception.PatOMat2Exception;
import cz.vse.swoe.ontodeside.patomat2.model.NewEntity;
import cz.vse.swoe.ontodeside.patomat2.model.NewEntityGenerator;
import cz.vse.swoe.ontodeside.patomat2.model.Pattern;
import cz.vse.swoe.ontodeside.patomat2.model.PatternInstance;
import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Finds and applies pattern matches in ontology.
 */
@SessionScope
@Service
public class MatchService {

    private final FileStorageService storageService;

    private final OntologyHolder ontologyHolder;

    private final NewEntityGenerator newEntityGenerator;

    private Map<String, Pattern> patterns;

    private Map<Integer, PatternInstance> matches;

    public MatchService(FileStorageService storageService, OntologyHolder ontologyHolder,
                        NewEntityGenerator newEntityGenerator) {
        this.storageService = storageService;
        this.ontologyHolder = ontologyHolder;
        this.newEntityGenerator = newEntityGenerator;
    }

    public List<PatternInstance> findMatches() {
        if (patterns == null) {
            throw new IncompleteTransformationInputException("Ontology not uploaded, yet.");
        }
        if (matches != null) {
            return new ArrayList<>(matches.values());
        }
        try {
            this.matches = new LinkedHashMap<>();
            return patterns.values().stream()
                           .map(ontologyHolder::findMatches)
                           .flatMap(List::stream)
                           .map(pm -> {
                               final Pattern p = pm.getPattern();
                               final List<NewEntity> newEntities = initNewEntities(pm);
                               final PatternInstance instance = new PatternInstance(pm.hashCode(), p.name(), pm,
                                       p.createTargetInsertSparql(pm, newEntities), p.createTargetDeleteSparql(pm),
                                       newEntities);
                               matches.put(pm.hashCode(), instance);
                               return instance;
                           })
                           .toList();
        } catch (RuntimeException e) {
            // Clear matches so that they can be reloaded
            this.matches = null;
            if (e instanceof PatOMat2Exception) {
                throw e;
            }
            throw new PatOMat2Exception(e);
        }
    }

    private List<NewEntity> initNewEntities(PatternMatch match) {
        final Set<String> newEntityVars = new HashSet<>(match.getPattern().targetVariables());
        newEntityVars.removeAll(match.getPattern().sourceVariables());
        return new ArrayList<>(newEntityVars.stream()
                                            .map(v -> newEntityGenerator.generateNewEntity(v, match.getPattern()
                                                                                                   .nameTransformations()
                                                                                                   .stream()
                                                                                                   .filter(nt -> nt.variableName()
                                                                                                                   .equals(v))
                                                                                                   .toList(), match))
                                            .toList());
    }

    private void loadOntology(String fileName) {
        final File ontologyFile = storageService.getFile(fileName);
        if (ontologyHolder.isLoaded()) {
            ontologyHolder.clear();
        }
        ontologyHolder.loadOntology(ontologyFile);
    }

    @EventListener
    public void onOntologyFileUploaded(OntologyFileUploadedEvent event) {
        loadOntology(event.getOntologyFileName());
        this.patterns = new LinkedHashMap<>();
        event.getPatterns().forEach(p -> patterns.put(p.name(), p));
        this.matches = null;
    }

    public Map<Integer, PatternInstance> getMatches() {
        if (matches == null) {
            findMatches();
        }
        return Collections.unmodifiableMap(matches);
    }

    public void clear() {
        this.matches = null;
    }
}
