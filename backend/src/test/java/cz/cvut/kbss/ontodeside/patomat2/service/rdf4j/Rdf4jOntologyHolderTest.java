package cz.cvut.kbss.ontodeside.patomat2.service.rdf4j;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Rdf4jOntologyHolderTest {

    @Test
    void getOntologyIriExtractsIriOfLoadedOntology() {
        final File file = new File(getClass().getClassLoader().getResource("archivo-partial.nt").getFile());
        final Rdf4jOntologyHolder sut = new Rdf4jOntologyHolder();
        sut.loadOntology(file);
        final Optional<String> result = sut.getOntologyIri();
        assertTrue(result.isPresent());
        assertEquals("https://w3id.org/cdc", result.get());
    }

    @Test
    void getLabelResolvesResourceLabel() {
        final File file = new File(getClass().getClassLoader().getResource("archivo-partial.nt").getFile());
        final Rdf4jOntologyHolder sut = new Rdf4jOntologyHolder();
        sut.loadOntology(file);
        final Optional<String> result = sut.getLabel("https://w3id.org/cdc#MEPDesignDS");
        assertTrue(result.isPresent());
        assertEquals("MEP design dataset", result.get());
    }

    @Test
    void applyTransformationQueryAppliesSpecifiedSparqlUpdate() {
        final File file = new File(getClass().getClassLoader().getResource("archivo-partial.nt").getFile());
        final Rdf4jOntologyHolder sut = new Rdf4jOntologyHolder();
        sut.loadOntology(file);
        final String subject = SKOS.CONCEPT.stringValue();
        final String label = "Concept";
        final String sparqlUpdate = "INSERT DATA { <" + subject + "> rdfs:label \"" + label + "\" . }";
        sut.applyTransformationQuery(sparqlUpdate);
        final Optional<String> result = sut.getLabel(subject);
        assertTrue(result.isPresent());
        assertEquals(label, result.get());
    }

    @Test
    void exportExportsCurrentRepositoryContent() throws Exception {
        final File file = new File(getClass().getClassLoader().getResource("archivo-partial.nt").getFile());
        final Rdf4jOntologyHolder sut = new Rdf4jOntologyHolder();
        sut.loadOntology(file);
        final ByteArrayOutputStream result = sut.export("application/n-triples");
        assertNotNull(result);
        final Model expectedModel = Rio.parse(new FileInputStream(file), RDFFormat.NTRIPLES);
        final Model actualModel = Rio.parse(new ByteArrayInputStream(result.toByteArray()), RDFFormat.NTRIPLES);
        assertTrue(Models.isomorphic(expectedModel, actualModel));
    }
}
