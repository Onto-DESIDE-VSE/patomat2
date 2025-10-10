package cz.vse.swoe.ontodeside.patomat2.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Summary of the performed transformation")
public record TransformationSummary(
        @Schema(description = "Statements added during the transformation (in n-triples)") String addedStatements,
        @Schema(description = "Statements removed during the transformation (in n-triples)") String removedStatements,
        @Schema(description = "New entities added during the transformation") List<NewEntity> addedEntities) {
}
