package cz.cvut.kbss.ontodeside.patomat2.service.rdf4j;

import cz.cvut.kbss.ontodeside.patomat2.exception.AmbiguousOntologyException;
import cz.cvut.kbss.ontodeside.patomat2.exception.BlankNodeResultException;
import cz.cvut.kbss.ontodeside.patomat2.exception.OntologyReadException;
import cz.cvut.kbss.ontodeside.patomat2.exception.PatOMat2Exception;
import cz.cvut.kbss.ontodeside.patomat2.model.OntologyDiff;
import cz.cvut.kbss.ontodeside.patomat2.model.Pattern;
import cz.cvut.kbss.ontodeside.patomat2.model.PatternMatch;
import cz.cvut.kbss.ontodeside.patomat2.model.ResultBinding;
import cz.cvut.kbss.ontodeside.patomat2.service.OntologyHolder;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.util.RepositoryUtil;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SessionScope
@Component
public class Rdf4jOntologyHolder implements OntologyHolder {

    private static final Logger LOG = LoggerFactory.getLogger(Rdf4jOntologyHolder.class);

    private String ontologyFileName;

    private final Repository repository = new SailRepository(new MemoryStore());

    @Override
    public boolean isLoaded() {
        return ontologyFileName != null;
    }

    @Override
    public void loadOntology(@NonNull File ontologyFile) {
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
    public Optional<String> getLabel(@NonNull String iri) {
        Objects.requireNonNull(iri);
        try (final RepositoryConnection conn = repository.getConnection()) {
            final ValueFactory vf = conn.getValueFactory();
            final RepositoryResult<Statement> result = conn.getStatements(vf.createIRI(iri), RDFS.LABEL, null);
            if (result.hasNext()) {
                String label = result.next().getObject().stringValue();
                result.close();
                return Optional.of(label);
            }
            result.close();
        } catch (RuntimeException e) {
            LOG.warn("Unable to resolve label of <{}>.", iri, e);
            return Optional.empty();
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
                    try {
                        bindings.getBindingNames().forEach(name -> row.add(toResultBinding(name, bindings)));
                    } catch (BlankNodeResultException e) {
                        LOG.warn("Skipping result because it contains a blank node.");
                        continue;
                    }
                    result.add(new PatternMatch(pattern, row));
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
        if (value.isBNode()) {
            throw new BlankNodeResultException("Value " + value + " is a blank node");
        }
        final String datatype = value.isResource() ? RDFS.RESOURCE.stringValue() : ((Literal) value).getDatatype()
                                                                                                    .stringValue();
        return new ResultBinding(name, strValue, datatype);
    }

    @Override
    public void applyTransformationQuery(@NonNull String sparqlUpdate) {
        Objects.requireNonNull(sparqlUpdate);
        verifyOntologyLoaded();
        try (final RepositoryConnection conn = repository.getConnection()) {
            conn.prepareUpdate(sparqlUpdate).execute();
        }
    }

    @Override
    public ByteArrayOutputStream export(@NonNull String mimeType) {
        final RDFFormat format = resolveRDFFormat(mimeType);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (final RepositoryConnection conn = repository.getConnection()) {
            final RDFWriter writer = Rio.createWriter(format, bos);
            conn.export(writer);
        }
        return bos;
    }

    private RDFFormat resolveRDFFormat(String mimeType) {
        if (RDFFormat.TURTLE.hasMIMEType(mimeType)) {
            return RDFFormat.TURTLE;
        } else if (RDFFormat.RDFXML.hasMIMEType(mimeType)) {
            return RDFFormat.RDFXML;
        } else if (RDFFormat.NTRIPLES.hasMIMEType(mimeType)) {
            return RDFFormat.NTRIPLES;
        } else if (RDFFormat.NQUADS.hasMIMEType(mimeType)) {
            return RDFFormat.NQUADS;
        } else if (RDFFormat.JSONLD.hasMIMEType(mimeType)) {
            return RDFFormat.JSONLD;
        } else if (RDFFormat.N3.hasMIMEType(mimeType)) {
            return RDFFormat.N3;
        } else {
            throw new PatOMat2Exception("Unsupported export MIME type: " + mimeType);
        }
    }

    @Override
    public void clear() {
        this.ontologyFileName = null;
        try (final RepositoryConnection conn = repository.getConnection()) {
            conn.clear();
        }
    }

    @Override
    public OntologyDiff difference(@NonNull File otherOntologyFile) {
        Objects.requireNonNull(otherOntologyFile);
        verifyOntologyLoaded();
        final Repository otherRepository = new SailRepository(new MemoryStore());
        try (final RepositoryConnection otherConn = otherRepository.getConnection()) {
            otherConn.add(otherOntologyFile);
        } catch (IOException e) {
            throw new PatOMat2Exception("Unable to load ontology for diff.", e);
        }
        final Collection<? extends Statement> added = RepositoryUtil.difference(repository, otherRepository);
        final Collection<? extends Statement> removed = RepositoryUtil.difference(otherRepository, repository);
        otherRepository.shutDown();

        return new OntologyDiff(serializeToNTriples(added), serializeToNTriples(removed));
    }

    private static String serializeToNTriples(Collection<? extends Statement> statements) {
        final Model m = new LinkedHashModel(statements);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Rio.write(m, bos, RDFFormat.NTRIPLES);
        return bos.toString();
    }
}
