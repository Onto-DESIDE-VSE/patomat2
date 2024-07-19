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

    public PatternParser() {
        configureJsonPath();
    }

    /**
     * Reads pattern from the specified file.
     *
     * @param patternFile File containing a pattern
     * @return Pattern read from the file
     * @throws PatternParserException When unable to read pattern
     */
    public Pattern readPattern(@NonNull File patternFile) {
        final Pattern p = new Pattern();
        try {
            final DocumentContext doc = JsonPath.parse(patternFile);
            p.setName(doc.read("$.tp.name", String.class));
            p.setSourceTriples(readTriples(doc, "op_source"));
            p.setTargetTriples(readTriples(doc, "op_target"));
        } catch (IOException e) {
            throw new PatternParserException("Unable to read pattern from " + patternFile, e);
        }
        return p;
    }

    private List<String> readTriples(DocumentContext doc, String parentNode) {
        return doc.read("$.." + parentNode + ".triples.triple.*", new TypeRef<>() {});
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
