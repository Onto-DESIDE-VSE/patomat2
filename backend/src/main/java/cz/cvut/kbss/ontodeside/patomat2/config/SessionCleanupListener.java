package cz.cvut.kbss.ontodeside.patomat2.config;


import cz.cvut.kbss.ontodeside.patomat2.Constants;
import cz.cvut.kbss.ontodeside.patomat2.service.FileStorageService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Cleans user data up after session ends.
 */
public class SessionCleanupListener implements HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(SessionCleanupListener.class);

    private final FileStorageService fileStorageService;

    public SessionCleanupListener(FileStorageService fileStorageService) {this.fileStorageService = fileStorageService;}


    @Override
    public void sessionCreated(HttpSessionEvent se) {
        LOG.debug("Created new session {}.", se.getSession().getId());
        se.getSession().setMaxInactiveInterval(Constants.SESSION_TIMEOUT);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        final HttpSession session = se.getSession();
        LOG.debug("Cleaning up destroyed session {}.", session.getId());
        final String ontologyFile = (String) session.getAttribute(Constants.ONTOLOGY_FILE_SESSION_ATTRIBUTE);
        final List<String> patternFiles = (List<String>) session.getAttribute(Constants.PATTERNS_SESSION_ATTRIBUTE);
        if (ontologyFile != null) {
            fileStorageService.deleteFile(ontologyFile);
        }
        patternFiles.forEach(fileStorageService::deleteFile);
    }
}
