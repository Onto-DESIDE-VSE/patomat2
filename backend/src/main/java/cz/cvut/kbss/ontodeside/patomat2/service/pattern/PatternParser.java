package cz.cvut.kbss.ontodeside.patomat2.service.pattern;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import cz.cvut.kbss.ontodeside.patomat2.exception.PatternParserException;
import cz.cvut.kbss.ontodeside.patomat2.model.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Reads pattern files, extracting the patterns they contain.
 */
@Component
public class PatternParser {

    private static final Logger LOG = LoggerFactory.getLogger(PatternParser.class);

    public PatternParser() {
        configureJsonPath();
    }

    private enum TripleSource {
        SOURCE("op_source"),
        TARGET("op_target");

        private final String name;

        TripleSource(String name) {
            this.name = name;
        }
    }

    /**
     * Reads pattern from the specified file.
     *
     * @param patternFile File containing a pattern
     * @return Pattern read from the file
     * @throws PatternParserException When unable to read pattern
     */
    public Pattern readPattern(@NonNull File patternFile) {
        LOG.debug("Parsing pattern from file {}.", patternFile.getName());
        try {
            final DocumentContext doc = JsonPath.parse(patternFile);
            final String name = doc.read("$.tp.name", String.class);
            final List<String> sourceTriples = readTriples(name, doc, TripleSource.SOURCE);
            final List<String> targetTriples = readTriples(name, doc, TripleSource.TARGET);
            LOG.info("Parsed pattern {} from file {}.", name, patternFile.getName());
            return new Pattern(name, sourceTriples, targetTriples);
        } catch (IOException e) {
            throw new PatternParserException("Unable to read pattern from " + patternFile, e);
        }
    }

    private List<String> readTriples(String patternName, DocumentContext doc, TripleSource source) {
        final List<String> triples = doc.read("$.." + source.name + ".triples.triple.*", new TypeRef<>() {});
        if (triples.isEmpty()) {
            throw new PatternParserException("No " + (source == TripleSource.SOURCE ? "source" : "target") + " triples found in pattern " + patternName);
        }
        return triples;
    }

    private static void configureJsonPath() {
        Configuration.setDefaults(new Configuration.Defaults() {

            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }
}
