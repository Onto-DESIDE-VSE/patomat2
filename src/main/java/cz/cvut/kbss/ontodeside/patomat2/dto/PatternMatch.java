package cz.cvut.kbss.ontodeside.patomat2.dto;

import cz.cvut.kbss.ontodeside.patomat2.service.pattern.ResultBinding;

import java.util.ArrayList;
import java.util.List;

public class PatternMatch {

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

    public void addBinding(ResultBinding binding) {
        bindings.add(binding);
    }

    public void setBindings(List<ResultBinding> bindings) {
        this.bindings = bindings;
    }
}
