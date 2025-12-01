package cz.vse.swoe.ontodeside.patomat2.service.rdf4j;

import cz.vse.swoe.ontodeside.patomat2.exception.AmbiguousOntologyException;
import cz.vse.swoe.ontodeside.patomat2.exception.OntologyReadException;
import cz.vse.swoe.ontodeside.patomat2.exception.PatOMat2Exception;
import cz.vse.swoe.ontodeside.patomat2.model.OntologyDiff;
import cz.vse.swoe.ontodeside.patomat2.model.Pattern;
import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
import cz.vse.swoe.ontodeside.patomat2.model.ResultBinding;
import cz.vse.swoe.ontodeside.patomat2.service.OntologyHolder;
import org.eclipse.rdf4j.model.BNode;
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
import org.jetbrains.annotations.NotNull;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TrigDocumentFormat;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@SessionScope
@Component
public class Rdf4jOntologyHolder implements OntologyHolder {

    private static final Logger LOG = LoggerFactory.getLogger(Rdf4jOntologyHolder.class);

    private String ontologyFileName;

    private boolean resolveImports;

    private final Repository repository = new SailRepository(new MemoryStore());

    @Override
    public boolean isLoaded() {
        return ontologyFileName != null;
    }

    @Override
    public boolean isLoadedWithImports() {
        return isLoaded() && resolveImports;
    }

    @Override
    public void loadOntology(@NonNull File ontologyFile, boolean resolveImports) {
        Objects.requireNonNull(ontologyFile);
        this.ontologyFileName = ontologyFile.getName();
        this.resolveImports = resolveImports;
        try (final RepositoryConnection conn = repository.getConnection()) {
            try {
                if (resolveImports) {
                    conn.add(loadOntologyWithImports(ontologyFile), RDFFormat.TRIG);
                } else {
                    conn.add(ontologyFile);
                }
            } catch (IOException e) {
                throw new OntologyReadException("Unable to load ontology file " + ontologyFile.getName(), e);
            }
        }
    }

    /**
     * Uses OWLAPI to load the ontology and transitively resolve all its imports.
     * <p>
     * Failed imports are only logged.
     *
     * @param ontologyFile Ontology file to import
     * @return Input stream containing the merged ontology (root ontology and all its imports) in TRIG format
     */
    private InputStream loadOntologyWithImports(File ontologyFile) {
        final OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        m.getOntologyConfigurator().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
        m.addMissingImportListener(e -> {
            LOG.warn("Unable to import ontology {}.", e.getImportedOntologyURI());
            LOG.debug("Error: {}", e.getCreationException().getMessage());
        });
        try {
            m.loadOntologyFromOntologyDocument(ontologyFile);
            final OWLOntology mergedOntology = new OWLOntologyMerger(m).createMergedOntology(m, null);
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mergedOntology.saveOntology(new TrigDocumentFormat(), bos);
            return new ByteArrayInputStream(bos.toByteArray());
        } catch (OWLException | OWLRuntimeException e) {
            LOG.error(e.getMessage(), e);
            throw new PatOMat2Exception("Unable to load ontology from file " + ontologyFile, e);
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
                    result.addAll(bindingSetToPatternMatches(bindings, pattern, conn));
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

    private List<PatternMatch> bindingSetToPatternMatches(BindingSet bs, Pattern pattern, RepositoryConnection conn) {
        final Map<String, List<ResultBinding>> row = new HashMap<>();
        bs.getBindingNames().forEach(name -> row.put(name, toResultBindingWithLabel(name, bs, conn)));
        return cartesianProduct(new ArrayList<>(row.values()), 0).flatMap(l -> Stream.of(new PatternMatch(pattern, l)))
                                                                 .toList();
    }

    private Stream<List<ResultBinding>> cartesianProduct(List<List<ResultBinding>> sets, int index) {
        if (index == sets.size()) {
            List<ResultBinding> emptyList = new ArrayList<>();
            return Stream.of(emptyList);
        }
        List<ResultBinding> currentSet = sets.get(index);
        return currentSet.stream().flatMap(element -> cartesianProduct(sets, index + 1)
                .map(list -> {
                    List<ResultBinding> newList = new ArrayList<>(list);
                    newList.addFirst(element);
                    return newList;
                }));
    }

    static List<ResultBinding> toResultBinding(String name, BindingSet bs, RepositoryConnection conn) {
        return toResultBinding(name, bs, conn, Optional.empty());
    }

    private static List<ResultBinding> toResultBinding(String name, @NotNull BindingSet bs, RepositoryConnection conn,
                                                       Optional<String> label) {
        final Value value = bs.getValue(name);
        final String strValue = value.stringValue();
        if (value.isBNode()) {
            return new Rdf4jUnionOfResolver(conn).resolveUnionOf(name, (BNode) value);
        }
        final String datatype = value.isResource() ? RDFS.RESOURCE.stringValue() : ((Literal) value).getDatatype()
                                                                                                    .stringValue();
        return List.of(new ResultBinding(name, strValue, datatype, false, label));
    }

    List<ResultBinding> toResultBindingWithLabel(String name, BindingSet bs, RepositoryConnection conn) {
        return toResultBinding(name, bs, conn, getLabel(bs.getValue(name).stringValue()));
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
