package cz.vse.swoe.ontodeside.patomat2.service;

import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.event.OntologyFileUploadedEvent;
import cz.vse.swoe.ontodeside.patomat2.exception.IncompleteTransformationInputException;
import cz.vse.swoe.ontodeside.patomat2.exception.NotFoundException;
import cz.vse.swoe.ontodeside.patomat2.model.Pattern;
import cz.vse.swoe.ontodeside.patomat2.model.TransformationInput;
import cz.vse.swoe.ontodeside.patomat2.service.pattern.PatternParser;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
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
     * @param ontologyFile  Ontology file
     * @param patternsFiles List of pattern files
     */
    public void saveOntologyAndPatterns(@NonNull MultipartFile ontologyFile,
                                        @NonNull List<MultipartFile> patternsFiles) {
        if (patternsFiles.isEmpty()) {
            throw new IncompleteTransformationInputException("No transformation pattern files provided.");
        }
        LOG.info("Storing ontology file '{}' and {} patterns for session {}.", ontologyFile.getOriginalFilename(), patternsFiles.size(), session.getId());
        final File storedFile = storageService.saveFile(ontologyFile);
        session.setAttribute(Constants.ONTOLOGY_FILE_SESSION_ATTRIBUTE, storedFile.getName());
        final List<Pattern> patterns = patternsFiles
                .stream()
                .map(storageService::saveFile)
                .map(patternParser::readPattern)
                .toList();
        session.setAttribute(Constants.PATTERN_FILES_SESSION_ATTRIBUTE, patterns.stream().map(Pattern::fileName)
                                                                                .toList());
        eventPublisher.publishEvent(new OntologyFileUploadedEvent(this, storedFile.getName(), patterns));
    }

    /**
     * Saves ontology and patterns which are specified as URL from which they are to be loaded.
     *
     * @param input Transformation input specifying ontology and pattern urls
     */
    public void saveOntologyAndPatterns(@NonNull TransformationInput input) {
        if (input.getOntology() == null || input.getPatterns().isEmpty()) {
            throw new IncompleteTransformationInputException("No ontology or transformation pattern file URLs provided.");
        }
        LOG.info("Storing ontology from '{}' and {} patterns for session {}.", input.getOntology(), input.getPatterns()
                                                                                                         .size(), session.getId());
        final File storedFile = storageService.downloadAndSaveFile(input.getOntology());
        session.setAttribute(Constants.ONTOLOGY_FILE_SESSION_ATTRIBUTE, storedFile.getName());
        final List<Pattern> patterns = input.getPatterns().stream()
                                            .map(storageService::downloadAndSaveFile)
                                            .map(patternParser::readPattern)
                                            .toList();
        session.setAttribute(Constants.PATTERN_FILES_SESSION_ATTRIBUTE, patterns.stream().map(Pattern::fileName)
                                                                                .toList());
        eventPublisher.publishEvent(new OntologyFileUploadedEvent(this, storedFile.getName(), patterns));
    }

    /**
     * Gets the stored transformation input.
     *
     * @return Transformation input representation
     */
    public TransformationInput getTransformationInput() {
        try {
            final TransformationInput result = new TransformationInput();
            result.setOntology(getRequiredUploadedOntologyFileName());
            result.setPatterns((List<String>) session.getAttribute(Constants.PATTERN_FILES_SESSION_ATTRIBUTE));
            return result;
        } catch (IncompleteTransformationInputException e) {
            throw new NotFoundException("Ontology not uploaded, yet.");
        }
    }

    /**
     * Gets the stored ontology file.
     *
     * @return Ontology file
     * @throws IncompleteTransformationInputException if the ontology has not been uploaded
     */
    public Resource getOntologyFileContent() {
        return new FileSystemResource(storageService.getFile(getRequiredUploadedOntologyFileName()));
    }

    /**
     * Gets the uploaded ontology file as {@link File}.
     *
     * @return Ontology file
     */
    public File getOntologyFile() {
        return storageService.getFile(getRequiredUploadedOntologyFileName());
    }

    private String getRequiredUploadedOntologyFileName() {
        return Optional.ofNullable((String) session.getAttribute(Constants.ONTOLOGY_FILE_SESSION_ATTRIBUTE))
                       .orElseThrow(() -> new IncompleteTransformationInputException("Ontology has not been uploaded yet."));
    }

    /**
     * Checks whether an ontology file has been uploaded.
     *
     * @return {@code true} if ontology is available, {@code false} otherwise
     */
    public Optional<String> getUploadedOntologyFileName() {
        return Optional.ofNullable((String) session.getAttribute(Constants.ONTOLOGY_FILE_SESSION_ATTRIBUTE));
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
