package cz.cvut.kbss.ontodeside.patomat2.service;

import cz.cvut.kbss.ontodeside.patomat2.Constants;
import cz.cvut.kbss.ontodeside.patomat2.exception.OntologyNotUploadedException;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    /**
     * Stores the specified ontology and pattern files.
     * <p>
     * It saves the stored file paths in the session.
     *
     * @param ontology Ontology file
     * @param patterns List of pattern files
     */
    public void saveOntologyAndPatterns(MultipartFile ontology, List<MultipartFile> patterns) {
        LOG.info("Storing ontology file '{}' and {} patterns for session {}.", ontology.getOriginalFilename(), patterns.size(), session.getId());
        session.setAttribute(Constants.ONTOLOGY_FILE_ATTRIBUTE, storageService.saveFile(ontology).getName());
        session.setAttribute(Constants.PATTERN_FILES_ATTRIBUTE, patterns
                .stream()
                .map(storageService::saveFile)
                .map(File::getName)
                .toList());
    }

    /**
     * Gets the stored ontology file.
     *
     * @return Ontology file
     * @throws OntologyNotUploadedException if the ontology has not been uploaded
     */
    public Resource getOntologyFile() {
        final String ontologyFileName = (String) session.getAttribute(Constants.ONTOLOGY_FILE_ATTRIBUTE);
        if (ontologyFileName == null) {
            throw new OntologyNotUploadedException("Ontology has not been uploaded yet.");
        }
        return new FileSystemResource(storageService.getFile(ontologyFileName));
    }
}
