package cz.cvut.kbss.ontodeside.patomat2.service.rdf4j;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
}
