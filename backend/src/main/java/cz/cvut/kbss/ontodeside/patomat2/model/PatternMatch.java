package cz.cvut.kbss.ontodeside.patomat2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatternMatch {

    private int id;

    /**
     * File that contains the pattern represented by this match
     */
    private String patternName;

    /**
     * Bindings of values that represent the pattern match instance.
     */
    private List<ResultBinding> bindings = new ArrayList<>();

    public PatternMatch() {
    }

    public PatternMatch(String patternName, List<ResultBinding> bindings) {
        this.patternName = patternName;
        this.bindings = bindings;
        this.id = hashCode();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }

    public List<ResultBinding> getBindings() {
        return bindings;
    }

    public void setBindings(List<ResultBinding> bindings) {
        this.bindings = bindings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof PatternMatch that)) {return false;}
        return Objects.equals(getPatternName(), that.getPatternName()) && Objects.equals(getBindings(),
                that.getBindings());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPatternName(), getBindings());
    }
}
