package cz.cvut.kbss.ontodeside.patomat2.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InvalidSessionTracker {

    private static final Object VALUE = new Object();

    private final Map<String, Object> invalidSessions = new ConcurrentHashMap<>();

    public void addSession(String sessionId) {
        invalidSessions.put(sessionId, VALUE);
    }

    public void removeSession(String sessionId) {
        invalidSessions.remove(sessionId);
    }

    public boolean containsSession(String sessionId) {
        return invalidSessions.containsKey(sessionId);
    }
}
