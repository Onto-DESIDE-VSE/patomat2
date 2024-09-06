package cz.vse.swoe.ontodeside.patomat2.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Information about a loaded pattern.
 *
 * @param name     Name of the pattern
 * @param fileName Name of the file containing the pattern
 */
@Schema(description = "Information about a loaded pattern.")
public record PatternInfo(
        @Schema(description = "Name of the pattern") String name,
        @Schema(description = "Name of the file containing the pattern") String fileName) {
}
