package cz.vse.swoe.ontodeside.patomat2.model;

import java.util.List;

public class LoadedTransformationInput {

    private String ontologyFile;

    private List<String> patternFiles;

    public String getOntologyFile() {
        return ontologyFile;
    }

    public void setOntologyFile(String ontologyFile) {
        this.ontologyFile = ontologyFile;
    }

    public List<String> getPatternFiles() {
        return patternFiles;
    }

    public void setPatternFiles(List<String> patternFiles) {
        this.patternFiles = patternFiles;
    }
}
