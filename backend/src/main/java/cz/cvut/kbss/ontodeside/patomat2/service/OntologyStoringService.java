package cz.cvut.kbss.ontodeside.patomat2.service;

import cz.cvut.kbss.ontodeside.patomat2.Constants;
import cz.cvut.kbss.ontodeside.patomat2.event.OntologyFileUploadedEvent;
import cz.cvut.kbss.ontodeside.patomat2.exception.OntologyNotUploadedException;
import cz.cvut.kbss.ontodeside.patomat2.model.Pattern;
import cz.cvut.kbss.ontodeside.patomat2.service.pattern.PatternParser;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class OntologyStoringService implements ApplicationEventPublisherAware {

    private static final Logger LOG = LoggerFactory.getLogger(OntologyStoringService.class);

    private final FileStorageService storageService;

    private final PatternParser patternParser;

    private final HttpSession session;

    private ApplicationEventPublisher eventPublisher;

    public OntologyStoringService(FileStorageService storageService, PatternParser patternParser, HttpSession session) {
        this.storageService = storageService;
        this.patternParser = patternParser;
        this.session = session;
    }

    /**
     * Stores the specified ontology and pattern files.
     * <p>
     * It saves the stored file paths in the session.
     *
     * @param ontology      Ontology file
     * @param patternsFiles List of pattern files
     */
    public void saveOntologyAndPatterns(MultipartFile ontology, List<MultipartFile> patternsFiles) {
        LOG.info("Storing ontology file '{}' and {} patterns for session {}.", ontology.getOriginalFilename(), patternsFiles.size(), session.getId());
        final File storedFile = storageService.saveFile(ontology);
        session.setAttribute(Constants.ONTOLOGY_FILE_SESSION_ATTRIBUTE, storedFile.getName());
        final List<Pattern> patterns = patternsFiles
                .stream()
                .map(storageService::saveFile)
                .map(patternParser::readPattern)
                .toList();
        session.setAttribute(Constants.PATTERN_FILES_SESSION_ATTRIBUTE, patterns.stream().map(Pattern::fileName).toList());
        eventPublisher.publishEvent(new OntologyFileUploadedEvent(this, storedFile.getName(), patterns));
    }

    /**
     * Gets the stored ontology file.
     *
     * @return Ontology file
     * @throws OntologyNotUploadedException if the ontology has not been uploaded
     */
    public Resource getOntologyFile() {
        final String ontologyFileName = (String) session.getAttribute(Constants.ONTOLOGY_FILE_SESSION_ATTRIBUTE);
        if (ontologyFileName == null) {
            throw new OntologyNotUploadedException("Ontology has not been uploaded yet.");
        }
        return new FileSystemResource(storageService.getFile(ontologyFileName));
    }

    /**
     * Checks whether an ontology file has been uploaded.
     *
     * @return {@code true} if ontology is available, {@code false} otherwise
     */
    public Optional<String> getUploadedOntologyFileName() {
        return Optional.ofNullable((String) session.getAttribute(Constants.ONTOLOGY_FILE_SESSION_ATTRIBUTE));
    }

    public void removeUploadedOntology(String ontologyFilename) {
        storageService.deleteFile(ontologyFilename, session.getId());
        session.removeAttribute(Constants.ONTOLOGY_FILE_SESSION_ATTRIBUTE);
    }

    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
