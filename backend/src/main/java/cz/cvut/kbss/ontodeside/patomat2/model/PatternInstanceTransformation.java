package cz.cvut.kbss.ontodeside.patomat2.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

/**
 * Specifies how a pattern instance should be transformed.
 * <p>
 * More specifically, it contains information how to augment the default generated transformation.
 */
@Schema(description = "Specifies how a particular pattern instance should be transformed")
public class PatternInstanceTransformation {

    /**
     * Pattern instance id
     */
    @Schema(description = "Pattern instance identifier")
    private Integer id;

    /**
     * Labels of new entities to override the generated ones.
     * <p>
     * The map maps variable names to entity labels.
     */
    @Schema(description = "Labels of new entities to override the generated ones. It is a map of variable names to lists of entity labels")
    private Map<String, List<String>> newEntityLabels;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Map<String, List<String>> getNewEntityLabels() {
        return newEntityLabels;
    }

    public void setNewEntityLabels(Map<String, List<String>> newEntityLabels) {
        this.newEntityLabels = newEntityLabels;
    }
}
