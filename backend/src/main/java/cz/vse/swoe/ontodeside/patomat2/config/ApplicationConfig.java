package cz.vse.swoe.ontodeside.patomat2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "patomat2")
public class ApplicationConfig {

    private String storage;

    private String corsAllowedOrigins;

    private String newEntityIriBase;

    private Security security = new Security();

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getCorsAllowedOrigins() {
        return corsAllowedOrigins;
    }

    public void setCorsAllowedOrigins(String corsAllowedOrigins) {
        this.corsAllowedOrigins = corsAllowedOrigins;
    }

    public String getNewEntityIriBase() {
        return newEntityIriBase;
    }

    public void setNewEntityIriBase(String newEntityIriBase) {
        this.newEntityIriBase = newEntityIriBase;
    }

    public static class Security {

        /**
         * Maximum number of concurrent sessions in the application.
         * <p>
         * Used to limit the number of loaded ontologies to prevent memory or disk space exhaustion.
         */
        private int maxSessions = 20;

        public int getMaxSessions() {
            return maxSessions;
        }

        public void setMaxSessions(int maxSessions) {
            this.maxSessions = maxSessions;
        }
    }
}
