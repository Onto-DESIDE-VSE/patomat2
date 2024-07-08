package cz.cvut.kbss.ontodeside.patomat2.service;

import cz.cvut.kbss.ontodeside.patomat2.config.ApplicationConfig;
import cz.cvut.kbss.ontodeside.patomat2.exception.InvalidFileException;
import cz.cvut.kbss.ontodeside.patomat2.exception.PatOMat2Exception;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.stream.Stream;

@Service
public class FileStorageService {

    private static final Logger LOG = LoggerFactory.getLogger(FileStorageService.class);

    private final ApplicationConfig config;

    private final HttpSession session;

    public FileStorageService(ApplicationConfig config, HttpSession session) {
        this.config = config;
        this.session = session;
    }

    @PostConstruct
    void init() {
        final File storageDir = new File(config.getStorage());
        if (!storageDir.exists()) {
            LOG.debug("Creating storage directory '{}'.", storageDir.getAbsolutePath());
            storageDir.mkdirs();
        } else {
            LOG.debug("Storage directory '{}' already exists. Cleaning it up.", storageDir.getAbsolutePath());
            if (!storageDir.isDirectory()) {
                throw new PatOMat2Exception("Storage directory must be a directory.");
            }
            Stream.of(storageDir.listFiles()).forEach(f -> {
                try {
                    Files.walk(f.toPath())
                         .sorted(Comparator.reverseOrder())
                         .map(Path::toFile)
                         .forEach(File::delete);
                } catch (IOException e) {
                    LOG.error("Unable to delete pre-existing ontology files.", e);
                }
            });

        }
    }

    /**
     * Stores the specified file.
     *
     * @param file File to messageStore
     * @return The newly created file on the host system
     */
    public File saveFile(MultipartFile file) {
        final File targetDir = new File(config.getStorage() + File.separator + session.getId());
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        File newFile = new File(targetDir + File.separator + sanitizeFilename(file.getOriginalFilename()));
        try {
            LOG.debug("Storing file '{}' at '{}'.", file.getOriginalFilename(), newFile.getAbsolutePath());
            file.transferTo(newFile.getAbsoluteFile());
        } catch (Exception e) {
            LOG.error("Unable to messageStore file at {}", newFile.getAbsolutePath(), e);
            throw new PatOMat2Exception("Unable to messageStore file " + file.getOriginalFilename());
        }
        return newFile;
    }

    private static String sanitizeFilename(String fileName) {
        // Replace accented characters with ASCII equivalents
        fileName = Normalizer.normalize(fileName, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        // Remove special characters
        fileName = fileName.replaceAll("[^a-zA-Z0-9.\\-_]", "");

        if (fileName.indexOf('.') == -1) {
            throw new InvalidFileException("File name '" + fileName + "' does not contain an extension.");
        }

        // Truncate the file name if it exceeds a maximum allowed length
        final int dotIndex = fileName.lastIndexOf('.');
        assert dotIndex > 0;
        if (dotIndex > 100) {
            String extension = fileName.substring(fileName.lastIndexOf('.'));
            fileName = fileName.substring(0, 100) + extension;
        }

        // Replace spaces with hyphens
        fileName = fileName.replaceAll("\\s", "-");

        // Remove any leading or trailing hyphens
        return fileName.replaceAll("(^-|-$)+", "");
    }

    /**
     * Retrieves a file from the configured storage directory.
     *
     * @param fileName the name of the file to retrieve
     * @return the File object representing the retrieved file
     * @throws PatOMat2Exception if the file does not exist in the storage directory
     */
    public File getFile(String fileName) {
        final File result = new File(config.getStorage() + File.separator + session.getId() + File.separator + fileName);
        if (!result.exists()) {
            throw new PatOMat2Exception("File " + fileName + " does not exist.");
        }
        return result;
    }

    /**
     * Deletes the specified file if it exists.
     *
     * @param fileName the name of the file to delete
     */
    public void deleteFile(String fileName) {
        final File result = new File(config.getStorage() + File.separator + session.getId() + File.separator + fileName);
        if (!result.exists()) {
            // Nothing to delete
            return;
        }
        result.delete();
        LOG.debug("Deleted file '{}'.", fileName);
    }
}
