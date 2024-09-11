package cz.vse.swoe.ontodeside.patomat2.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Describes the transformation input provided by the user.
 *
 * @param ontology Ontology to be transformed. This can be a URL or a file name
 * @param patterns List of transformation patterns. These can be URLs or file names
 */
@Schema(description = "Describes the transformation input provided by the user.")
public record TransformationInput(@Schema(description = "Ontology to be transformed (name of the ontology file).")
                                  String ontology,
                                  @Schema(description = "List of pattern files.")
                                  List<String> patterns) {

    @Override
    public List<String> patterns() {
        return patterns != null ? patterns : List.of();
    }
}
