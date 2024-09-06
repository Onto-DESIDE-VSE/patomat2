package cz.vse.swoe.ontodeside.patomat2.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Information about the loaded transformation input.
 * @param ontology Ontology file
 * @param patterns Loaded patterns info
 */
@Schema(description = "Information about the loaded transformation input.")
public record LoadedTransformationInput(
        @Schema(description = "Ontology file")
        String ontology,
        @Schema(description = "Loaded patterns info")
        List<PatternInfo> patterns) {
}
