package cz.vse.swoe.ontodeside.patomat2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public record PatternInstance(int id, @JsonIgnore Pattern pattern, PatternMatch match, String sparqlInsert, String sparqlDelete,
                              List<NewEntity> newEntities) {

    public PatternInstance deepCopy() {
        return new PatternInstance(id, pattern, match, sparqlInsert, sparqlDelete, new ArrayList<>(newEntities.stream()
                                                                                                                .map(ne -> new NewEntity(ne.variableName(), ne.identifier(), ne.labels()))
                                                                                                                .toList()));
    }

    @JsonProperty
    public String patternName() {
        return pattern.name();
    }

    public boolean hasDelete() {
        return sparqlDelete != null;
    }
}
