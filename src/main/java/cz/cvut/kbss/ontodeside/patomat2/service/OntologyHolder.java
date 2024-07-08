package cz.cvut.kbss.ontodeside.patomat2.service;

import cz.cvut.kbss.ontodeside.patomat2.service.pattern.ResultRow;

import java.io.File;
import java.util.List;

public interface OntologyHolder {

    void loadOntology(File ontologyFile);

    List<ResultRow> findMatches(File patternFile);
}
