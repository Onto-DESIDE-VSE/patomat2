package cz.cvut.kbss.ontodeside.patomat2.service;

import cz.cvut.kbss.ontodeside.patomat2.Constants;
import cz.cvut.kbss.ontodeside.patomat2.dto.PatternMatch;
import cz.cvut.kbss.ontodeside.patomat2.event.OntologyFileUploadedEvent;
import cz.cvut.kbss.ontodeside.patomat2.exception.OntologyNotUploadedException;
import jakarta.servlet.http.HttpSession;
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

    private final HttpSession session;

    private final OntologyHolder ontologyHolder;

    private Map<Integer, PatternMatch> matches;

    public MatchService(FileStorageService storageService, HttpSession session, OntologyHolder ontologyHolder) {
        this.storageService = storageService;
        this.session = session;
        this.ontologyHolder = ontologyHolder;
    }

    public List<PatternMatch> findMatches() {
        final String ontologyFileName = (String) session.getAttribute(Constants.ONTOLOGY_FILE_ATTRIBUTE);
        if (ontologyFileName == null) {
            throw new OntologyNotUploadedException("Ontology has not been uploaded yet.");
        }
        if (!ontologyHolder.isLoaded(ontologyFileName)) {
            final File ontologyFile = storageService.getFile(ontologyFileName);
            ontologyHolder.loadOntology(ontologyFile);
            this.matches = new LinkedHashMap<>();
        } else if (!matches.isEmpty()) {
            return new ArrayList<>(matches.values());
        }
        final List<String> patternFiles = (List<String>) session.getAttribute(Constants.PATTERN_FILES_ATTRIBUTE);
        return patternFiles.stream()
                           .map(storageService::getFile)
                           .map(ontologyHolder::findMatches)
                           .flatMap(List::stream)
                           .peek(pm -> matches.put(pm.hashCode(), pm))
                           .toList();
    }

    @EventListener
    public void onOntologyFileUploaded(OntologyFileUploadedEvent event) {
        if (matches != null) {
            this.matches = null;
            ontologyHolder.clear();
        }
    }
}
