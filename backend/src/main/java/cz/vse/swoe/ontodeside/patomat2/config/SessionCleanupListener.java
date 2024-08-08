package cz.vse.swoe.ontodeside.patomat2.config;


import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.service.FileStorageService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cleans user data up after session ends.
 */
public class SessionCleanupListener implements HttpSessionListener {

    private static final Logger LOG = LoggerFactory.getLogger(SessionCleanupListener.class);

    private final AtomicInteger sessionCounter = new AtomicInteger();

    private final ApplicationConfig config;

    private final InvalidSessionTracker invalidSessionTracker;

    private final FileStorageService fileStorageService;

    public SessionCleanupListener(ApplicationConfig config, InvalidSessionTracker invalidSessionTracker, FileStorageService fileStorageService) {
        this.config = config;
        this.invalidSessionTracker = invalidSessionTracker;
        this.fileStorageService = fileStorageService;
    }


    @Override
    public void sessionCreated(HttpSessionEvent se) {
        if (sessionCounter.get() > config.getSecurity().getMaxSessions()) {
            LOG.warn("Too many sessions. Marking session '{}' as invalid.", se.getSession().getId());
            invalidSessionTracker.addSession(se.getSession().getId());
            // set ultra-short inactive interval so that it can be discarded immediately
            se.getSession().setMaxInactiveInterval(1);
            return;
        }
        LOG.debug("Created new session {}, current session count: {}.", se.getSession()
                                                                          .getId(), sessionCounter.incrementAndGet());
        se.getSession().setMaxInactiveInterval(Constants.SESSION_TIMEOUT);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        final HttpSession session = se.getSession();
        if (invalidSessionTracker.containsSession(session.getId())) {
            invalidSessionTracker.removeSession(session.getId());
            return;
        }
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
