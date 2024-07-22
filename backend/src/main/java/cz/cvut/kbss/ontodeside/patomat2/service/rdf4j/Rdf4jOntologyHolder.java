package cz.cvut.kbss.ontodeside.patomat2.service.rdf4j;

import cz.cvut.kbss.ontodeside.patomat2.exception.AmbiguousOntologyException;
import cz.cvut.kbss.ontodeside.patomat2.exception.OntologyReadException;
import cz.cvut.kbss.ontodeside.patomat2.model.Pattern;
import cz.cvut.kbss.ontodeside.patomat2.model.PatternMatch;
import cz.cvut.kbss.ontodeside.patomat2.model.ResultBinding;
import cz.cvut.kbss.ontodeside.patomat2.service.OntologyHolder;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SessionScope
@Component
public class Rdf4jOntologyHolder implements OntologyHolder {

    private String ontologyFileName;

    private final Repository repository = new SailRepository(new MemoryStore());

    @Override
    public boolean isLoaded() {
        return ontologyFileName != null;
    }

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
    public Optional<String> getOntologyIri() {
        verifyOntologyLoaded();
        try (final RepositoryConnection conn = repository.getConnection()) {
            final TupleQuery q = conn.prepareTupleQuery("SELECT ?o WHERE { ?o a ?ontology . }");
            q.setBinding("ontology", OWL.ONTOLOGY);

            try (final TupleQueryResult result = q.evaluate()) {
                if (result.hasNext()) {
                    final BindingSet bs = result.next();
                    if (result.hasNext()) {
                        throw new AmbiguousOntologyException("Multiple ontologies found when trying to get ontology IRI");
                    }
                    return Optional.of(bs.getValue("o").stringValue());
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<PatternMatch> findMatches(@NonNull Pattern pattern) {
        Objects.requireNonNull(pattern);
        verifyOntologyLoaded();
        final List<PatternMatch> result = new ArrayList<>();
        try (final RepositoryConnection conn = repository.getConnection()) {
            final TupleQuery tq = conn.prepareTupleQuery(pattern.sourceSparql());
            try (final TupleQueryResult tqResult = tq.evaluate()) {
                for (BindingSet bindings : tqResult) {
                    final List<ResultBinding> row = new ArrayList<>();
                    bindings.getBindingNames().forEach(name -> row.add(toResultBinding(name, bindings)));
                    result.add(new PatternMatch(pattern.name(), row));
                }
            }
        }
        return result;
    }

    private void verifyOntologyLoaded() {
        if (ontologyFileName == null || ontologyFileName.isBlank()) {
            throw new IllegalStateException("Ontology has not been loaded.");
        }
    }

    private static ResultBinding toResultBinding(String name, BindingSet bs) {
        final Value value = bs.getValue(name);
        final String strValue = value.stringValue();
        final String datatype = value.isResource() ? RDFS.RESOURCE.stringValue() : ((Literal) value).getDatatype()
                                                                                                    .stringValue();
        return new ResultBinding(name, strValue, datatype);
    }

    @Override
    public void clear() {
        this.ontologyFileName = null;
        try (final RepositoryConnection conn = repository.getConnection()) {
            conn.clear();
        }
    }
}
