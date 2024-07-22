package cz.cvut.kbss.ontodeside.patomat2.service.rdf4j;

import cz.cvut.kbss.ontodeside.patomat2.config.ApplicationConfig;
import cz.cvut.kbss.ontodeside.patomat2.service.OntologyHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Rdf4jNewEntityGeneratorTest {

    @Mock
    private OntologyHolder ontologyHolder;

    @Spy
    private ApplicationConfig configuration = new ApplicationConfig();

    @InjectMocks
    private Rdf4jNewEntityGenerator sut;

    @Test
    void generateIdentifierGeneratesIdentifierUsingOntologyUriAndRandomNumber() {
        when(ontologyHolder.isLoaded()).thenReturn(true);
        final String ontologyIri = "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#";
        when(ontologyHolder.getOntologyIri()).thenReturn(Optional.of(ontologyIri));
        final URI result = sut.generateIdentifier();
        assertNotNull(result);
        assertThat(result.toString(), startsWith(ontologyIri));
        assertThat(result.toString().length(), greaterThan(ontologyIri.length()));
    }

    @Test
    void generateIdentifierUsesConfiguredBaseUriWhenLoadedFileContainsNoExplicitOntology() {
        final String base = "https://example.com/patomat2/";
        configuration.setNewEntityIriBase(base);
        when(ontologyHolder.isLoaded()).thenReturn(true);
        when(ontologyHolder.getOntologyIri()).thenReturn(Optional.empty());
        final URI result = sut.generateIdentifier();
        assertNotNull(result);
        assertThat(result.toString(), startsWith(base));
        assertThat(result.toString().length(), greaterThan(base.length()));
    }
}
