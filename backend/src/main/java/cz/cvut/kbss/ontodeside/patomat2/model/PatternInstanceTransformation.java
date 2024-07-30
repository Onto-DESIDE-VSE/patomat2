package cz.cvut.kbss.ontodeside.patomat2.model;

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
     * Labels of new entities to override the generated ones
     */
    private Map<String, String> newEntityLabels;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Map<String, String> getNewEntityLabels() {
        return newEntityLabels;
    }

    public void setNewEntityLabels(Map<String, String> newEntityLabels) {
        this.newEntityLabels = newEntityLabels;
    }
}
