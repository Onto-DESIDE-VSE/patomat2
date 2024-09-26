package cz.vse.swoe.ontodeside.patomat2.service;

import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.event.OntologyFileUploadedEvent;
import cz.vse.swoe.ontodeside.patomat2.exception.IncompleteTransformationInputException;
import cz.vse.swoe.ontodeside.patomat2.exception.NotFoundException;
import cz.vse.swoe.ontodeside.patomat2.model.LoadedTransformationInput;
import cz.vse.swoe.ontodeside.patomat2.model.Pattern;
import cz.vse.swoe.ontodeside.patomat2.model.PatternInfo;
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
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
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
     * Saves ontology and patterns for transformation.
     * <p>
     * If the ontology file is provided, it takes precedence over the URL from which it is to be loaded specified in
     * {@code input}.
     * <p>
     * Patterns are loaded both from the specified files and from the URLs specified in {@code input}.
     *
     * @param ontologyFile  File containing the source ontology, optional
     * @param patternsFiles List of files containing transformation patterns, possibly empty
     * @param input         Object specifying URLs of ontology and patterns to download
     */
    public void saveOntologyAndPatterns(@Nullable MultipartFile ontologyFile,
                                        @NonNull List<MultipartFile> patternsFiles,
                                        @NonNull TransformationInput input) {
        if (ontologyFile == null && (input.ontology() == null || input.ontology().isBlank())) {
            throw new IncompleteTransformationInputException("No source ontology has been provided.");
        }
        if (patternsFiles.isEmpty() && input.patterns().isEmpty()) {
            throw new IncompleteTransformationInputException("No transformation patterns have been provided.");
        }
        final File storedOntology = storeOntology(ontologyFile, input);
        final List<Pattern> patterns = storePatterns(patternsFiles, input);
        LOG.info("Stored ontology '{}' and {} patterns for session {}.", storedOntology.getName(), patterns.size(), session.getId());
        eventPublisher.publishEvent(new OntologyFileUploadedEvent(this, session.getAttribute(Constants.ONTOLOGY_FILE_SESSION_ATTRIBUTE)
                                                                               .toString(), patterns));
    }

    private File storeOntology(MultipartFile ontologyFile, TransformationInput input) {
        final File storedFile;
        if (ontologyFile != null) {
            storedFile = storageService.saveFile(ontologyFile);
        } else {
            storedFile = storageService.downloadAndSaveFile(input.ontology());
        }
        session.setAttribute(Constants.ONTOLOGY_FILE_SESSION_ATTRIBUTE, storedFile.getName());
        return storedFile;
    }

    private List<Pattern> storePatterns(List<MultipartFile> patternsFiles, TransformationInput input) {
        final List<Pattern> patternsFromFiles = patternsFiles
                .stream()
                .map(storageService::saveFile)
                .map(patternParser::readPattern)
                .toList();
        final List<Pattern> patternsFromUrls = input.patterns().stream()
                                                    .map(storageService::downloadAndSaveFile)
                                                    .map(patternParser::readPattern)
                                                    .toList();
        final List<Pattern> patterns = new ArrayList<>(patternsFromFiles);
        patterns.addAll(patternsFromUrls);
        session.setAttribute(Constants.PATTERN_FILES_SESSION_ATTRIBUTE, patterns.stream()
                                                                                .map(p -> new PatternInfo(p.name(), p.fileName()))
                                                                                .toList());
        return patterns;
    }

    /**
     * Saves ontology and patterns which are specified as URL from which they are to be loaded.
     *
     * @param input Transformation input specifying ontology and pattern urls
     */
    public void saveOntologyAndPatterns(@NonNull TransformationInput input) {
        if (input.ontology() == null || input.patterns().isEmpty()) {
            throw new IncompleteTransformationInputException("No ontology or transformation pattern file URLs provided.");
        }
        LOG.info("Storing ontology from '{}' and {} patterns for session {}.", input.ontology(), input.patterns()
                                                                                                      .size(), session.getId());
        final File storedFile = storageService.downloadAndSaveFile(input.ontology());
        session.setAttribute(Constants.ONTOLOGY_FILE_SESSION_ATTRIBUTE, storedFile.getName());
        final List<Pattern> patterns = input.patterns().stream()
                                            .map(storageService::downloadAndSaveFile)
                                            .map(patternParser::readPattern)
                                            .toList();
        session.setAttribute(Constants.PATTERN_FILES_SESSION_ATTRIBUTE, patterns.stream()
                                                                                .map(p -> new PatternInfo(p.name(), p.fileName()))
                                                                                .toList());
        eventPublisher.publishEvent(new OntologyFileUploadedEvent(this, storedFile.getName(), patterns));
    }

    /**
     * Gets the stored transformation input.
     *
     * @return Transformation input representation
     */
    public LoadedTransformationInput getTransformationInput() {
        try {
            return new LoadedTransformationInput(getRequiredUploadedOntologyFileName(), (List<PatternInfo>) session.getAttribute(Constants.PATTERN_FILES_SESSION_ATTRIBUTE));
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
