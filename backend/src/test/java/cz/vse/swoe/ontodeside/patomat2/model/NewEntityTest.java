package cz.vse.swoe.ontodeside.patomat2.model;

import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.environment.Generator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NewEntityTest {

    @Test
    void createSparqlInsertCreatesInsertWithAllSpecifiedLabels() {
        final String id = Generator.generateUri().toString();
        NewEntity newEntity = new NewEntity("variable", id, List.of(new EntityLabel("label1", Constants.DEFAULT_LABEL_PROPERTY), new EntityLabel("label2", Constants.DEFAULT_LABEL_PROPERTY)));
        String expected = "INSERT DATA { <" + id + "> <" + Constants.DEFAULT_LABEL_PROPERTY + "> \"label1\" . <" + id + "> <" + Constants.DEFAULT_LABEL_PROPERTY + "> \"label2\" . }";
        assertEquals(expected, newEntity.createInsertLabelSparql());
    }

    @Test
    void createSparqlInsertCreatesInsertWithLabelsThatAreToBeApplied() {
        final String id = Generator.generateUri().toString();
        NewEntity newEntity = new NewEntity("variable", id, List.of(new EntityLabel("label1", Constants.DEFAULT_LABEL_PROPERTY, true), new EntityLabel("label2", Constants.DEFAULT_LABEL_PROPERTY, false)));
        String expected = "INSERT DATA { <" + id + "> <" + Constants.DEFAULT_LABEL_PROPERTY + "> \"label1\" . }";
        assertEquals(expected, newEntity.createInsertLabelSparql());
    }
}
