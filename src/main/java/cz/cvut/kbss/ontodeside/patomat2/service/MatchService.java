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

    public MatchService(FileStorageService storageService, HttpSession session) {this.storageService = storageService;
        this.session = session;
    }

    public List<PatternMatch> findMatches() {
        final String ontologyFileName = (String) session.getAttribute(Constants.ONTOLOGY_FILE_ATTRIBUTE);
        if (ontologyFileName == null) {
            throw new OntologyNotUploadedException("Ontology has not been uploaded yet.");
        }
        final List<String> patternFiles = (List<String>) session.getAttribute(Constants.PATTERN_FILES_ATTRIBUTE);
        final File ontologyFile = storageService.getFile(ontologyFileName);
        return null;
    }
}
