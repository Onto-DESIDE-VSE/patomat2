package cz.cvut.kbss.ontodeside.patomat2.model;

import java.util.List;
import java.util.Map;

/**
 * Specifies how a pattern instance should be transformed.
 * <p>
 * More specifically, it contains information how to augment the default generated transformation.
 */
public class PatternInstanceTransformation {

    /**
     * Pattern instance id
     */
    private Integer id;

    /**
     * Labels of new entities to override the generated ones.
     * <p>
     * The map maps variable names to entity labels.
     */
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
