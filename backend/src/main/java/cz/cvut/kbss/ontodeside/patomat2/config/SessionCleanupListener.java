package cz.cvut.kbss.ontodeside.patomat2.config;


import cz.cvut.kbss.ontodeside.patomat2.Constants;
import cz.cvut.kbss.ontodeside.patomat2.service.FileStorageService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cleans user data up after session ends.
 */
public class SessionCleanupListener implements HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(SessionCleanupListener.class);

    private final AtomicInteger sessionCounter = new AtomicInteger();

    private final ApplicationConfig config;

    private final FileStorageService fileStorageService;

    public SessionCleanupListener(ApplicationConfig config, FileStorageService fileStorageService) {
        this.config = config;
        this.fileStorageService = fileStorageService;
    }


    @Override
    public void sessionCreated(HttpSessionEvent se) {
        if (sessionCounter.get() > config.getSecurity().getMaxSessions()) {
            throw new SessionAuthenticationException("To many open sessions. Please try again later.");
        }
        LOG.debug("Created new session {}, current session count: {}.", se.getSession()
                                                                          .getId(), sessionCounter.incrementAndGet());
        se.getSession().setMaxInactiveInterval(Constants.SESSION_TIMEOUT);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        final HttpSession session = se.getSession();
        LOG.debug("Cleaning up destroyed session {}. Current session count: {}", session.getId(), sessionCounter.decrementAndGet());
        final String ontologyFile = (String) session.getAttribute(Constants.ONTOLOGY_FILE_SESSION_ATTRIBUTE);
        final List<String> patternFiles = (List<String>) session.getAttribute(Constants.PATTERN_FILES_SESSION_ATTRIBUTE);
        if (ontologyFile != null) {
            fileStorageService.deleteFile(ontologyFile, session.getId());
        }
        if (patternFiles != null) {
            patternFiles.forEach(pf -> fileStorageService.deleteFile(pf, session.getId()));
        }
    }
}
