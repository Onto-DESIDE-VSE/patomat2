package cz.cvut.kbss.ontodeside.patomat2.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Specifies how the pattern-based ontology transformation should be applied.
 */
@Schema(description = "Specifies how the pattern-based ontology transformation should be applied")
public class TransformationSpecification {

    /**
     * List of pattern instances to transform.
     */
    @Schema(description = "List of pattern instances to use in transformation")
    private List<PatternInstanceTransformation> patternInstances;

    /**
     * Whether to apply the delete queries
     */
    @Schema(description = "Whether to apply the delete queries")
    private boolean applyDeletes;

    public List<PatternInstanceTransformation> getPatternInstances() {
        return patternInstances;
    }

    public void setPatternInstances(
            List<PatternInstanceTransformation> patternInstances) {
        this.patternInstances = patternInstances;
    }

    public boolean isApplyDeletes() {
        return applyDeletes;
    }

    public void setApplyDeletes(boolean applyDeletes) {
        this.applyDeletes = applyDeletes;
    }
}
