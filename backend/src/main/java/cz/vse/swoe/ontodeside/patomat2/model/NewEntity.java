package cz.vse.swoe.ontodeside.patomat2.model;

import java.util.List;

public record NewEntity(String variableName, String identifier, List<EntityLabel> labels) {

    /**
     * Creates a SPARQL INSERT query that asserts labels of this new entity.
     *
     * @return SPARQL INSERT query
     */
    public String createInsertLabelSparql() {
        String labelPattern = "<%s> <%s> \"%s\" .";
        final StringBuilder sb = new StringBuilder("INSERT DATA { ");
        labels.stream().filter(EntityLabel::apply).forEach(l -> sb.append(String.format(labelPattern, identifier, l.property(), l.value())).append(" "));
        sb.append('}');
        return sb.toString();
    }

    public NewEntity withLabels(List<EntityLabel> labels) {
        return new NewEntity(variableName, identifier, labels);
    }
}
