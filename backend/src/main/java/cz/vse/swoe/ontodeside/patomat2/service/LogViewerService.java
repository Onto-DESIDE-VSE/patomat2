package cz.vse.swoe.ontodeside.patomat2.service;

import cz.vse.swoe.ontodeside.patomat2.exception.PatOMat2Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Allows viewing application log.
 */
@Service
public class LogViewerService {

    private static final Logger LOG = LoggerFactory.getLogger(LogViewerService.class);

    @Value("${logging.file.name}")
    private String logFileName;

    /**
     * Gets the contents of the application log file.
     *
     * @return Log string
     */
    public String getApplicationLog() {
        final Path logPath = Paths.get(logFileName);
        if (!Files.exists(logPath)) {
            throw new PatOMat2Exception("Log file does not exist.");
        }
        try {
            return Files.readString(logPath);
        } catch (IOException e) {
            LOG.error("Unable to read log file.", e);
            throw new PatOMat2Exception("Unable to read log file.");
        }
    }
}
