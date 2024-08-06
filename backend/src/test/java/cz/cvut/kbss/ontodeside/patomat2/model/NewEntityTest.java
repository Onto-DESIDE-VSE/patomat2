package cz.cvut.kbss.ontodeside.patomat2.model;

import cz.cvut.kbss.ontodeside.patomat2.environment.Generator;
import org.junit.jupiter.api.Test;

import java.awt.geom.GeneralPath;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NewEntityTest {

    @Test
    void createSparqlInsertCreatesInsertWithAllSpecifiedLabels() {
        final String id = Generator.generateUri().toString();
        NewEntity newEntity = new NewEntity("variable", id, List.of("label1", "label2"));
        String expected = "INSERT DATA { <" + id + "> rdfs:label \"label1\" . <" + id + "> rdfs:label \"label2\" . }";
        assertEquals(expected, newEntity.createInsertLabelSparql());
    }

}
