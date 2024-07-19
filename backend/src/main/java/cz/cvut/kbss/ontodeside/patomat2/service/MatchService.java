package cz.cvut.kbss.ontodeside.patomat2.service;

import cz.cvut.kbss.ontodeside.patomat2.model.Pattern;
import cz.cvut.kbss.ontodeside.patomat2.model.PatternMatch;
import cz.cvut.kbss.ontodeside.patomat2.event.OntologyFileUploadedEvent;
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

    private Map<String, Pattern> patterns;

    private Map<Integer, PatternMatch> matches;

    public MatchService(FileStorageService storageService, OntologyHolder ontologyHolder) {
        this.storageService = storageService;
        this.ontologyHolder = ontologyHolder;
    }

    public List<PatternMatch> findMatches() {
        if (matches != null) {
            return new ArrayList<>(matches.values());
        }
        this.matches = new LinkedHashMap<>();
        return patterns.values().stream()
                       .map(ontologyHolder::findMatches)
                       .flatMap(List::stream)
                       .peek(pm -> matches.put(pm.hashCode(), pm))
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
