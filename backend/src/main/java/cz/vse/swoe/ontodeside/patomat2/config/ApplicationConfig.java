package cz.vse.swoe.ontodeside.patomat2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "patomat2")
public class ApplicationConfig {

    /**
     * Path to a folder where temporary files (namely transformation input for individual sessions) should be stored.
     */
    private String storage;

    /**
     * Allowed origins for CORS. Important for local development.
     */
    private String corsAllowedOrigins;

    /**
     * Base for generating new entity IRIs.
     */
    private String newEntityIriBase;

    private Security security = new Security();

    private List<Example> examples;

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

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public List<Example> getExamples() {
        return examples;
    }

    public void setExamples(List<Example> examples) {
        this.examples = examples;
    }

    /**
     * Security configuration.
     */
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

    /**
     * Configuration of an example transformation input.
     */
    public static class Example {

        /**
         * Name of the example
         */
        private String name;

        /**
         * URL from which to download the example ontology.
         */
        private String ontology;

        /**
         * URLs from which to download the example transformation patterns.
         */
        private List<String> patterns;

        public Example() {
        }

        public Example(String name, String ontology, List<String> patterns) {
            this.name = name;
            this.ontology = ontology;
            this.patterns = patterns;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOntology() {
            return ontology;
        }

        public void setOntology(String ontology) {
            this.ontology = ontology;
        }

        public List<String> getPatterns() {
            return patterns != null ? patterns : List.of();
        }

        public void setPatterns(List<String> patterns) {
            this.patterns = patterns;
        }
    }
}
