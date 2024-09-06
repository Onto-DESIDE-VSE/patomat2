package cz.vse.swoe.ontodeside.patomat2.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Describes the transformation input provided by the user.
 */
@Schema(description = "Describes the transformation input provided by the user.")
public class TransformationInput {

    @Schema(description = "Ontology to be transformed (name of the ontology file).")
    private String ontology;

    @Schema(description = "List of pattern files.")
    private List<String> patterns = new ArrayList<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransformationInput that)) {
            return false;
        }
        return Objects.equals(getOntology(), that.getOntology()) && Objects.equals(getPatterns(), that.getPatterns());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOntology(), getPatterns());
    }
}
