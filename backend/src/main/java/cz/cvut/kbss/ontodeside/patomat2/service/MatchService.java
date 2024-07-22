package cz.cvut.kbss.ontodeside.patomat2.service;

import cz.cvut.kbss.ontodeside.patomat2.event.OntologyFileUploadedEvent;
import cz.cvut.kbss.ontodeside.patomat2.exception.OntologyNotUploadedException;
import cz.cvut.kbss.ontodeside.patomat2.model.NewEntityGenerator;
import cz.cvut.kbss.ontodeside.patomat2.model.Pattern;
import cz.cvut.kbss.ontodeside.patomat2.model.PatternInstance;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
             throw new OntologyNotUploadedException("Ontology not uploaded, yet.");
        }
        if (matches != null) {
            return new ArrayList<>(matches.values());
        }
        this.matches = new LinkedHashMap<>();
        return patterns.values().stream()
                       .map(ontologyHolder::findMatches)
                       .flatMap(List::stream)
                       .map(pm -> {
                           final Pattern p = pm.getPattern();
                           final PatternInstance instance = new PatternInstance(pm.hashCode(), p.name(), pm,
                                   p.createTargetInsertSparql(pm, newEntityGenerator), p.createTargetDeleteSparql(pm));
                           matches.put(pm.hashCode(), instance);
                           return instance;
                       })
                       .toList();
    }

    private void loadOntology(String fileName) {
        final File ontologyFile = storageService.getFile(fileName);
        ontologyHolder.loadOntology(ontologyFile);
    }

    @EventListener
    public void onOntologyFileUploaded(OntologyFileUploadedEvent event) {
        loadOntology(event.getOntologyFileName());
        this.patterns = new LinkedHashMap<>();
        event.getPatterns().forEach(p -> patterns.put(p.name(), p));
        this.matches = null;
    }
}
