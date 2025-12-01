package cz.vse.swoe.ontodeside.patomat2.service.rdf4j;

import cz.vse.swoe.ontodeside.patomat2.model.Pattern;
import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Rdf4jOntologyHolderTest {

    @Test
    void getOntologyIriExtractsIriOfLoadedOntology() {
        final File file = new File(getClass().getClassLoader().getResource("archivo-partial.nt").getFile());
        final Rdf4jOntologyHolder sut = new Rdf4jOntologyHolder();
        sut.loadOntology(file, false);
        final Optional<String> result = sut.getOntologyIri();
        assertTrue(result.isPresent());
        assertEquals("https://w3id.org/cdc", result.get());
    }

    @Test
    void getLabelResolvesResourceLabel() {
        final File file = new File(getClass().getClassLoader().getResource("archivo-partial.nt").getFile());
        final Rdf4jOntologyHolder sut = new Rdf4jOntologyHolder();
        sut.loadOntology(file, false);
        final Optional<String> result = sut.getLabel("https://w3id.org/cdc#MEPDesignDS");
        assertTrue(result.isPresent());
        assertEquals("MEP design dataset", result.get());
    }

    @Test
    void applyTransformationQueryAppliesSpecifiedSparqlUpdate() {
        final File file = new File(getClass().getClassLoader().getResource("archivo-partial.nt").getFile());
        final Rdf4jOntologyHolder sut = new Rdf4jOntologyHolder();
        sut.loadOntology(file, false);
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
        sut.loadOntology(file, false);
        final ByteArrayOutputStream result = sut.export("application/n-triples");
        assertNotNull(result);
        final Model expectedModel = Rio.parse(new FileInputStream(file), RDFFormat.NTRIPLES);
        final Model actualModel = Rio.parse(new ByteArrayInputStream(result.toByteArray()), RDFFormat.NTRIPLES);
        assertTrue(Models.isomorphic(expectedModel, actualModel));
    }

    @Test
    void findMatchesReplacesBlankNodeRepresentingUnionWithUnionConstituents() {
        final File file = new File(getClass().getClassLoader().getResource("cmt.owl").getFile());
        final Rdf4jOntologyHolder sut = new Rdf4jOntologyHolder();
        sut.loadOntology(file, false);
        final Pattern p = new Pattern("pattern.json", "Pattern", List.of("?p rdfs:domain ?A",
                "?p rdfs:range ?B",
                "?C rdfs:subClassOf ?B"), List.of(), List.of(), List.of(), List.of());

        final List<PatternMatch> result = sut.findMatches(p);
        final List<PatternMatch> blankNodeOnes = result.stream().filter(PatternMatch::isBasedOnBlankNode).toList();
        assertFalse(blankNodeOnes.isEmpty());
        assertTrue(blankNodeOnes.stream()
                                .anyMatch(pm -> pm.getBinding("A").isPresent() && pm.getBinding("A").get().value()
                                                                                    .equals("http://cmt/#Reviewer")));
        assertTrue(blankNodeOnes.stream()
                                .anyMatch(pm -> pm.getBinding("A").isPresent() && pm.getBinding("A").get().value()
                                                                                    .equals("http://cmt/#Chairman")));
        assertTrue(blankNodeOnes.stream()
                                .anyMatch(pm -> pm.getBinding("A").isPresent() && pm.getBinding("A").get().value()
                                                                                    .equals("http://cmt/#Author")));
    }

    @Test
    void loadOntologyWithImportsResolvingLoadsOntologyAndItsImports() {
        final File file = new File(getClass().getClassLoader().getResource("ceon_plan.ttl").getFile());
        final Rdf4jOntologyHolder sut = new Rdf4jOntologyHolder();
        sut.loadOntology(file, true);

        final Optional<String> eventLabel = sut.getLabel("http://w3id.org/CEON/ontology/processODP/Event");
        assertTrue(eventLabel.isPresent());
        assertEquals("Event", eventLabel.get());
    }
}
