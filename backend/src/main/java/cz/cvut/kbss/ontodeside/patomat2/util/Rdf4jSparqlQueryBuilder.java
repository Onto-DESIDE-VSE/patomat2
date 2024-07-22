package cz.cvut.kbss.ontodeside.patomat2.util;

import cz.cvut.kbss.ontodeside.patomat2.Constants;
import cz.cvut.kbss.ontodeside.patomat2.model.PatternMatch;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.sparql.query.QueryStringUtil;
import org.eclipse.rdf4j.repository.sparql.query.SPARQLQueryBindingSet;

public class Rdf4jSparqlQueryBuilder {

    /**
     * Takes a SPARQL Update (INSERT/DELETE) statement and {@link PatternMatch} instance and returns a query string
     * where variables in the query are bound to the values in the {@link PatternMatch}.
     *
     * @param statement SPARQL Update statement
     * @param instance  Pattern match
     * @return SPARQL query string
     */
    public static String populateSparqlUpdate(String statement, PatternMatch instance) {
        final ValueFactory vf = SimpleValueFactory.getInstance();
        final SPARQLQueryBindingSet bindings = new SPARQLQueryBindingSet();
        instance.getBindings().forEach(b -> bindings.addBinding(b.name(), Constants.RDFS_RESOURCE.equals(b.datatype()) ?
                vf.createIRI(b.value()) : vf.createLiteral(b.value(), vf.createIRI(b.datatype()))));
        return QueryStringUtil.getUpdateString(statement, bindings);
    }
}
