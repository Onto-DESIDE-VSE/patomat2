package cz.cvut.kbss.ontodeside.patomat2.model;

public record PatternInstance(int id, String patternName, PatternMatch match, String sparqlInsert, String sparqlDelete) {
}
