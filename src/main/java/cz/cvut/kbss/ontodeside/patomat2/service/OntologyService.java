package cz.cvut.kbss.ontodeside.patomat2.service;

import cz.cvut.kbss.ontodeside.patomat2.Constants;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class OntologyService {

    private static final Logger LOG = LoggerFactory.getLogger(OntologyService.class);

    private final FileStorageService storageService;

    private final HttpSession session;

    public OntologyService(FileStorageService storageService, HttpSession session) {
        this.storageService = storageService;
        this.session = session;
    }

    public void saveOntologyAndPatterns(MultipartFile ontology, List<MultipartFile> patterns) {
        LOG.info("Storing ontology file '{}' and {} patterns for session {}.", ontology.getOriginalFilename(), patterns.size(), session.getId());
        session.setAttribute(Constants.ONTOLOGY_FILE_ATTRIBUTE, storageService.saveFile(ontology));
        session.setAttribute(Constants.PATTERN_FILES_ATTRIBUTE, patterns
                .stream()
                .map(storageService::saveFile)
                .toList());
    }
}
