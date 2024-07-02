package cz.cvut.kbss.ontodeside.patomat2.service;

import cz.cvut.kbss.ontodeside.patomat2.config.ApplicationConfig;
import cz.cvut.kbss.ontodeside.patomat2.exception.InvalidFileException;
import cz.cvut.kbss.ontodeside.patomat2.exception.PatOMat2Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class FileStorageService {

    private static final Logger LOG = LoggerFactory.getLogger(FileStorageService.class);

    private final ApplicationConfig config;

    public FileStorageService(ApplicationConfig config) {this.config = config;}

    /**
     * Stores the specified file.
     *
     * @param file File to store
     * @return The newly created file on the host system
     */
    public File saveFile(MultipartFile file) {
        final File targetDir = new File(config.getStorage());
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        File newFile = new File(targetDir + File.separator + sanitizeFilename(file.getOriginalFilename()));
        try {
            LOG.trace("Storing file '{}' at '{}'.", file.getOriginalFilename(), newFile.getAbsolutePath());
            file.transferTo(newFile);
        } catch (Exception e) {
            LOG.error("Unable to store file at {}", newFile.getAbsolutePath(), e);
            throw new PatOMat2Exception("Unable to store file " + file.getOriginalFilename());
        }
        return newFile;
    }

    private static String sanitizeFilename(String fileName) {
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
        final File result = new File(config.getStorage() + File.separator + fileName);
        if (!result.exists()) {
            throw new PatOMat2Exception("File " + fileName + " does not exist.");
        }
        return result;
    }
}
