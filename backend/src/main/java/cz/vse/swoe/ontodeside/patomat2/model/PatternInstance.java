package cz.vse.swoe.ontodeside.patomat2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public record PatternInstance(int id, @JsonIgnore Pattern pattern, PatternMatch match, String sparqlInsert,
                              String sparqlDelete,
                              List<NewEntity> newEntities) {

    public PatternInstance deepCopy() {
        return new PatternInstance(id, pattern, match, sparqlInsert, sparqlDelete, newEntities.stream()
                                                                                              .map(ne -> new NewEntity(ne.variableName(), ne.identifier(), ne.labels()))
                                                                                              .collect(Collectors.toList()));
    }

    @JsonProperty
    public String patternName() {
        return pattern.name();
    }

    public boolean hasDelete() {
        return sparqlDelete != null;
    }

    public PatternInstance withNewEntities(List<NewEntity> newEntities) {
        return new PatternInstance(id, pattern, match, sparqlInsert, sparqlDelete, newEntities);
    }
}
