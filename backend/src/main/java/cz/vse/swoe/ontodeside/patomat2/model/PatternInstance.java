package cz.vse.swoe.ontodeside.patomat2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public record PatternInstance(int id, @JsonIgnore Pattern pattern, PatternMatch match, String sparqlInsert, String sparqlDelete,
                              List<NewEntity> newEntities, Integer likertScore) {

    public PatternInstance(int id, Pattern pattern, PatternMatch match, String sparqlInsert, String sparqlDelete,
                           List<NewEntity> newEntities) {
        this(id, pattern, match, sparqlInsert, sparqlDelete, newEntities, null);
    }

    public PatternInstance deepCopy() {
        return new PatternInstance(id, pattern, match, sparqlInsert, sparqlDelete, new ArrayList<>(newEntities.stream()
                                                                                                                .map(ne -> new NewEntity(ne.variableName(), ne.identifier(), ne.labels()))
                                                                                                                .toList()), likertScore);
    }

    public PatternInstance withLikertScore(int likertScore) {
        return new PatternInstance(id, pattern, match, sparqlInsert, sparqlDelete, newEntities, likertScore);
    }

    @JsonProperty
    public String patternName() {
        return pattern.name();
    }

    public boolean hasDelete() {
        return sparqlDelete != null;
    }
}
