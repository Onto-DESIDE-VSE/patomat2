package cz.vse.swoe.ontodeside.patomat2.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Objects;

/**
 * Describes the transformation input provided by the user.
 */
@Schema(description = "Describes the transformation input provided by the user.")
public class TransformationInput {

    @Schema(description = "Ontology to be transformed. If the ontology was uploaded, it is the name of the ontology file. If the ontology was provided by URL, it is the URL.")
    private String ontology;

    @Schema(description = "List of patterns. If the patterns were uploaded, they are the names of the pattern files. If the patterns were provided by URL, they are the URLs.")
    private List<String> patterns;

    @Schema(description = "Whether the input was provided by URL or uploaded.")
    private boolean fromUrl = false;

    public String getOntology() {
        return ontology;
    }

    public void setOntology(String ontology) {
        this.ontology = ontology;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

    public boolean isFromUrl() {
        return fromUrl;
    }

    public void setFromUrl(boolean fromUrl) {
        this.fromUrl = fromUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransformationInput that)) {
            return false;
        }
        return isFromUrl() == that.isFromUrl() && Objects.equals(getOntology(),
                that.getOntology()) && Objects.equals(
                getPatterns(), that.getPatterns());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOntology(), getPatterns(), isFromUrl());
    }
}
