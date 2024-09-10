package cz.vse.swoe.ontodeside.patomat2.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

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

    private Security security = new Security(20);

    private Example example = new Example();

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

    public Example getExample() {
        return example;
    }

    public void setExample(Example example) {
        this.example = example;
    }

    /**
     * Security configuration.
     *
     * @param maxSessions Maximum number of concurrent sessions in the application. Used to limit the number of loaded
     *                    ontologies to prevent memory or disk space exhaustion.
     */
    public record Security(int maxSessions) {
    }

    /**
     * Configuration of an example transformation input.
     *
     * @param ontology URL from which to download the example ontology.
     * @param patterns URLs from which to download the example transformation patterns.
     */
    public record Example(String ontology, List<String> patterns) {

        public Example() {
            this("", List.of());
        }
    }
}
