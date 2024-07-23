package cz.cvut.kbss.ontodeside.patomat2.model;

import java.util.List;

public record PatternInstance(int id, String patternName, PatternMatch match, String sparqlInsert, String sparqlDelete, List<NewEntity> newEntities) {
}
