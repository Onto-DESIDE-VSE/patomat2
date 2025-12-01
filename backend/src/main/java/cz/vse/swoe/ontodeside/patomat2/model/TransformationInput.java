package cz.vse.swoe.ontodeside.patomat2.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Describes the transformation input provided by the user.
 *
 * @param ontology       Ontology to be transformed. This can be a URL or a file name
 * @param patterns       List of URLs from which transformation patterns can be fetched
 * @param resolveImports Whether to automatically resolve {@literal owl:imports}. If false, only the root ontology is
 *                       loaded
 */
@Schema(description = "Describes the transformation input provided by the user.")
public record TransformationInput(@Schema(description = "Ontology to be transformed (name of the ontology file).")
                                  String ontology,
                                  @Schema(description = "List of URL to fetch patterns from.")
                                  List<String> patterns,
                                  @Schema(description = "Whether to automatically resolve owl:imports. If false, only the root ontology is loaded")
                                  boolean resolveImports) {

    @Override
    public List<String> patterns() {
        return patterns != null ? patterns : List.of();
    }
}
