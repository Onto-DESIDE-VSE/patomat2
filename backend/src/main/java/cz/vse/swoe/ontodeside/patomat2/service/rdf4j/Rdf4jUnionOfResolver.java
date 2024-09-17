package cz.vse.swoe.ontodeside.patomat2.service.rdf4j;

import cz.vse.swoe.ontodeside.patomat2.model.ResultBinding;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves constituents of a <literal>owl:unionOf</literal> axiom.
 */
class Rdf4jUnionOfResolver {

    private final RepositoryConnection conn;

    Rdf4jUnionOfResolver(RepositoryConnection conn) {
        this.conn = conn;
    }

    /**
     * Resolves constituents of a <literal>owl:unionOf</literal> axiom.
     *
     * @param variableName Name of the variable whose value is the union blank node
     * @param node         The union blank node
     * @return List of result bindings consisting of the union constituents
     */
    List<ResultBinding> resolveUnionOf(String variableName, BNode node) {

        final TupleQuery unionConstituentsQuery = conn.prepareTupleQuery("""
                PREFIX owl: <http://www.w3.org/2002/07/owl#>
                PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                SELECT ?x WHERE { ?node owl:unionOf/rdf:rest*/rdf:first ?x . }
                """
        );
        unionConstituentsQuery.setBinding("node", node);

        try (final TupleQueryResult result = unionConstituentsQuery.evaluate()) {
            final List<ResultBinding> resultBindings = new ArrayList<>();
            while (result.hasNext()) {
                final BindingSet bs = result.next();
                final List<ResultBinding> tmp = Rdf4jOntologyHolder.toResultBinding("x", bs, conn);
                resultBindings.addAll(tmp.stream()
                                         .map(b -> new ResultBinding(variableName, b.value(), b.datatype(), true))
                                         .toList());
            }
            return resultBindings;
        }
    }
}
