package cz.cvut.kbss.ontodeside.patomat2.model;

import java.util.ArrayList;
import java.util.List;

public record PatternInstance(int id, String patternName, PatternMatch match, String sparqlInsert, String sparqlDelete,
                              List<NewEntity> newEntities) {

    public PatternInstance deepCopy() {
        return new PatternInstance(id, patternName, match, sparqlInsert, sparqlDelete, new ArrayList<>(newEntities.stream()
                                                                                                                .map(ne -> new NewEntity(ne.variableName(), ne.identifier(), ne.label()))
                                                                                                                .toList()));
    }
}
