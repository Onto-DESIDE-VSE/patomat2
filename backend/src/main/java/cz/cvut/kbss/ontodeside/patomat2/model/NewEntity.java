package cz.cvut.kbss.ontodeside.patomat2.model;

public record NewEntity(String variableName, String identifier, String label) {

    /**
     * Creates a SPARQL INSERT query that asserts label of this new entity.
     *
     * @return SPARQL INSERT query
     */
    public String createInsertLabelSparql() {
        return String.format("INSERT DATA { <%s> rdfs:label \"%s\" . }", identifier, label);
    }
}
