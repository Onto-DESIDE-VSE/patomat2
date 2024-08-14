package cz.vse.swoe.ontodeside.patomat2.model.function;

import cz.vse.swoe.ontodeside.patomat2.Constants;
import cz.vse.swoe.ontodeside.patomat2.model.PatternMatch;
import cz.vse.swoe.ontodeside.patomat2.model.ResultBinding;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NominalizeFunctionTest {

    private final NominalizeFunction sut = new NominalizeFunction(null, null);

    @ParameterizedTest
    @MethodSource("verbSource")
    void applyTransformsVerbToNoun(String verb, String expected) {
        assertEquals(expected, sut.apply(new PatternMatch(), "nominalize(" + verb + ")"));
    }

    static Stream<Arguments> verbSource() {
        return Stream.of(
                Arguments.of("accept", "acceptation"),
                Arguments.of("admit", "admission"),
                Arguments.of("measure", "measure")
        );
    }

    @Test
    void applyTransformsVerbExtractedFromVariableValue() {
        final PatternMatch match = new PatternMatch(null, List.of(
                new ResultBinding("a", "http://dati.beniculturali.it/cis/includes", Constants.RDFS_RESOURCE)
        ));
        String result = sut.apply(match, "nominalize(?a)");
        assertEquals("inclusion", result);
    }

    @Test
    void applyReturnsNounIfPhraseDoesNotContainVerb() {
        final String result = sut.apply(new PatternMatch(), "nominalize(store)");
        assertEquals("store", result);
    }

    @Test
    void applyReturnsNounWhenUnableToNominalizePhraseVerb() {
        final PatternMatch match = new PatternMatch(null, List.of(
                new ResultBinding("a", "http://dati.beniculturali.it/cis/isMemberOf", Constants.RDFS_RESOURCE)
        ));
        String result = sut.apply(match, "nominalize(?a)");
        assertEquals("Member", result);
    }
}
