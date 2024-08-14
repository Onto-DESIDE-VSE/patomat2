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

import static org.junit.jupiter.api.Assertions.*;

class PassivizeFunctionTest {

    private final PassivizeFunction sut = new PassivizeFunction(null, null);

    @ParameterizedTest
    @MethodSource("regularVerbsSource")
    void applyPassivizesRegularVerbSpecifiedAsArgument(String argument, String expected) {
        final String result = sut.apply(new PatternMatch(), "passivize(" + argument + ")");
        assertEquals(expected, result);
    }

    static Stream<Arguments> regularVerbsSource() {
        return Stream.of(
                Arguments.of("accept", "accepted"),
                Arguments.of("try" , "tried"),
                Arguments.of("to store" , "stored")
        );
    }

    @Test
    void applyReturnsArgumentIfItIsAlreadyPassiveVerb() {
        final String result = sut.apply(new PatternMatch(), "passivize(stolen)");
        assertEquals("stolen", result);
    }

    @Test
    void applyPassivizesIrregularVerbSpecifiedAsArgument() {
        final String result = sut.apply(new PatternMatch(), "passivize(sees)");
        assertEquals("seen", result);
    }

    @Test
    void applyPassivizesVerbExtractedFromVariableValue() {
        final PatternMatch match = new PatternMatch(null, List.of(
                new ResultBinding("a", "http://dati.beniculturali.it/cis/takesPlaceDuring", Constants.RDFS_RESOURCE)
        ));
        String result = sut.apply(match, "passivize(?a)");
        assertEquals("taken", result);
    }

    @Test
    void applyGetsVerbForNounAndPassivizesIt() {
        final String result = sut.apply(new PatternMatch(), "passivize(acceptance)");
        assertEquals("accepted", result);
    }
}
