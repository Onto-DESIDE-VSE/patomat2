package cz.cvut.kbss.ontodeside.patomat2.service;

import cz.cvut.kbss.ontodeside.patomat2.Constants;
import cz.cvut.kbss.ontodeside.patomat2.dto.PatternMatch;
import cz.cvut.kbss.ontodeside.patomat2.exception.OntologyNotUploadedException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Finds and applies pattern matches in ontology.
 */
@Service
public class MatchService {

    private final FileStorageService storageService;

    private final HttpSession session;

    private final OntologyHolder ontologyHolder;

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
        }
        final List<String> patternFiles = (List<String>) session.getAttribute(Constants.PATTERN_FILES_ATTRIBUTE);
        return patternFiles.stream().map(storageService::getFile).map(f -> ontologyHolder.findMatches(f).stream()
                                                                                         .map(r -> new PatternMatch(f.getName(), r.getBindings()))
                                                                                         .toList())
                           .flatMap(List::stream).toList();
    }
}
