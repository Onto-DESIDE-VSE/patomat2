package cz.cvut.kbss.ontodeside.patomat2.dto;

import java.util.HashMap;
import java.util.Map;

public class PatternMatch {

    /**
     * File that contains the pattern represented by this match
     */
    private String patternFile;

    /**
     * Bindings of values that represent the pattern match instance.
     */
    private Map<String, String> bindings = new HashMap<>();

    public String getPatternFile() {
        return patternFile;
    }

    public void setPatternFile(String patternFile) {
        this.patternFile = patternFile;
    }

    public Map<String, String> getBindings() {
        return bindings;
    }

    public void addBinding(String key, String value) {
        bindings.put(key, value);
    }

    public void setBindings(Map<String, String> bindings) {
        this.bindings = bindings;
    }
}
