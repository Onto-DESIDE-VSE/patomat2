package cz.cvut.kbss.ontodeside.patomat2.model;

import java.util.List;

public class Pattern {

    private String name;

    private List<String> sourceTriples;

    private List<String> targetTriples;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSourceTriples() {
        return sourceTriples;
    }

    public void setSourceTriples(List<String> sourceTriples) {
        this.sourceTriples = sourceTriples;
    }

    public List<String> getTargetTriples() {
        return targetTriples;
    }

    public void setTargetTriples(List<String> targetTriples) {
        this.targetTriples = targetTriples;
    }
}
