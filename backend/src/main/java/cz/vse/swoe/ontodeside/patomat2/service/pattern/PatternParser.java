package cz.vse.swoe.ontodeside.patomat2.service.pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import cz.vse.swoe.ontodeside.patomat2.exception.PatternParserException;
import cz.vse.swoe.ontodeside.patomat2.model.NameTransformation;
import cz.vse.swoe.ontodeside.patomat2.model.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
            final List<String> filters = readFilters(name, doc);
            LOG.info("Parsed pattern {} from file {}.", name, patternFile.getName());
            return new Pattern(patternFile.getName(), name, sourceTriples, filters, targetTriples, readNameTransformations(doc));
        } catch (IOException e) {
            throw new PatternParserException("Unable to read pattern from " + patternFile, e);
        }
    }

    private List<String> readTriples(String patternName, DocumentContext doc, TripleSource source) {
        try {
            final List<String> triples = doc.read("$.." + source.name + ".triples.triple.*", new TypeRef<>() {});
            if (triples.isEmpty()) {
                throw new PatternParserException("No " + (source == TripleSource.SOURCE ? "source" : "target") + " triples found in pattern " + patternName);
            }
            return triples;
        } catch (PathNotFoundException e) {
            LOG.error("No triple patterns of type {} found in pattern {}.", source.name, patternName);
            return List.of();
        }
    }

    private List<String> readFilters(String patternName, DocumentContext doc) {
        try {
            return doc.read("$.." + TripleSource.SOURCE.name + ".filters.filter.*", new TypeRef<>() {});
        } catch (PathNotFoundException e) {
            // No big deal
            LOG.trace("No filters found in pattern {}.", patternName);
            return List.of();
        }
    }

    private List<NameTransformation> readNameTransformations(DocumentContext doc) {
        try {
            final ObjectNode nameTransformations = doc.read("$.tp.naming_transformation.ntp", new TypeRef<>() {});
            final Iterator<Map.Entry<String, JsonNode>> it = nameTransformations.fields();
            final List<NameTransformation> result = new ArrayList<>();
            while (it.hasNext()) {
                final Map.Entry<String, JsonNode> e = it.next();
                assert e.getKey().startsWith("?");
                if (e.getValue().isArray()) {
                    final ArrayNode array = (ArrayNode) e.getValue();
                    for (int i = 0; i < array.size(); i++) {
                        result.add(new NameTransformation(e.getKey().substring(1), array.get(i).asText()));
                    }
                } else {
                    result.add(new NameTransformation(e.getKey().substring(1), e.getValue().asText()));
                }
            }
            return result;
        } catch (PathNotFoundException e) {
            LOG.warn("No name transformations found in pattern.");
            return List.of();
        }
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
