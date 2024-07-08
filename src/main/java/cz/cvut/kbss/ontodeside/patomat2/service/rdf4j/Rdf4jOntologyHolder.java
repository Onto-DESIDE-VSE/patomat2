package cz.cvut.kbss.ontodeside.patomat2.service.rdf4j;

import cz.cvut.kbss.ontodeside.patomat2.exception.OntologyReadException;
import cz.cvut.kbss.ontodeside.patomat2.service.OntologyHolder;
import cz.cvut.kbss.ontodeside.patomat2.service.pattern.ResultBinding;
import cz.cvut.kbss.ontodeside.patomat2.service.pattern.ResultRow;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SessionScope
@Component
public class Rdf4jOntologyHolder implements OntologyHolder {

    private String ontologyFileName;

    private final Repository repository = new SailRepository(new MemoryStore());

    @Override
    public boolean isLoaded(String fileName) {
        return Objects.equals(ontologyFileName, fileName);
    }

    @Override
    public void loadOntology(File ontologyFile) {
        Objects.requireNonNull(ontologyFile);
        this.ontologyFileName = ontologyFile.getName();
        try (final RepositoryConnection conn = repository.getConnection()) {
            try {
                conn.add(ontologyFile);
            } catch (IOException e) {
                throw new OntologyReadException("Unable to load ontology file " + ontologyFile.getName(), e);
            }
        }
    }

    @Override
    public List<ResultRow> findMatches(File patternFile) {
        final String query;
        try {
            query = String.join("\n", Files.readAllLines(patternFile.toPath()));
        } catch (IOException e) {
            throw new OntologyReadException("Unable to load pattern file " + patternFile.getName(), e);
        }
        final List<ResultRow> result = new ArrayList<>();
        try (final RepositoryConnection conn = repository.getConnection()) {
            final TupleQuery tq = conn.prepareTupleQuery(query);
            try (final TupleQueryResult tqResult = tq.evaluate()) {
                for (BindingSet bindings : tqResult) {
                    final ResultRow row = new ResultRow();
                    bindings.getBindingNames()
                            .forEach(name -> row.addBinding(toResultBinding(name, bindings)));
                    result.add(row);
                }
            }
        }
        return result;
    }

    private static ResultBinding toResultBinding(String name, BindingSet bs) {
        final Value value = bs.getValue(name);
        final String strValue = value.stringValue();
        final String datatype = value.isResource() ? RDFS.RESOURCE.stringValue() : ((Literal) value).getDatatype()
                                                                                                    .stringValue();
        return new ResultBinding(name, strValue, datatype);
    }
}
