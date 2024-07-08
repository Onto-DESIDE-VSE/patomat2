package cz.cvut.kbss.ontodeside.patomat2.dto;

import cz.cvut.kbss.ontodeside.patomat2.service.pattern.ResultBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatternMatch {

    private int id;

    /**
     * File that contains the pattern represented by this match
     */
    private String patternFile;

    /**
     * Bindings of values that represent the pattern match instance.
     */
    private List<ResultBinding> bindings = new ArrayList<>();

    public PatternMatch() {
    }

    public PatternMatch(String patternFile, List<ResultBinding> bindings) {
        this.patternFile = patternFile;
        this.bindings = bindings;
        this.id = hashCode();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatternFile() {
        return patternFile;
    }

    public void setPatternFile(String patternFile) {
        this.patternFile = patternFile;
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
        return Objects.equals(getPatternFile(), that.getPatternFile()) && Objects.equals(getBindings(),
                that.getBindings());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPatternFile(), getBindings());
    }
}
