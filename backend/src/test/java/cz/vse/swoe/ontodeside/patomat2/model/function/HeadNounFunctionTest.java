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

class HeadNounFunctionTest {

    private final HeadNounFunction sut = new HeadNounFunction(null, null);

    @Test
    void applyExtractsHeadNounFromSpecifiedPhrase() {
        final String result = sut.apply(null, "head_noun(a book on the table)");
        assertEquals("book", result);
    }

    @ParameterizedTest
    @MethodSource("identifierSource")
    void applyExtractHeadNounFromIdentifierSpecifiedByVariable(String identifier, String expected) {
        final PatternMatch match = new PatternMatch(null, List.of(new ResultBinding("a", identifier, Constants.RDFS_RESOURCE)));
        final String result = sut.apply(match, "head_noun(?a)");
        assertEquals(expected, result);
    }

    protected static Stream<Arguments> identifierSource() {
        return Stream.of(
                Arguments.of("http://example.org/topConceptOf", "Concept"),
                Arguments.of("http://cmt/#hasConflictOfInterest", "Conflict"),
                Arguments.of("http://cmt/#finalizePaperAssignment", "Assignment"),
                Arguments.of("http://cmt/#Administrator", "Administrator")
        );
    }
}
