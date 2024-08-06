package cz.cvut.kbss.ontodeside.patomat2.model;

import java.util.List;

public record NewEntity(String variableName, String identifier, List<String> labels) {

    /**
     * Creates a SPARQL INSERT query that asserts labels of this new entity.
     *
     * @return SPARQL INSERT query
     */
    public String createInsertLabelSparql() {
        String labelPattern = "<%s> rdfs:label \"%s\" .";
        final StringBuilder sb = new StringBuilder("INSERT DATA { ");
        labels.forEach(l -> sb.append(String.format(labelPattern, identifier, l)).append(" "));
        sb.append('}');
        return sb.toString();
    }
}
