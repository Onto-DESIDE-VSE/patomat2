package cz.vse.swoe.ontodeside.patomat2.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Information about a pattern fetched from a remote URL
 *
 * @param name Pattern name
 * @param url  URL from which the pattern has been fetched
 */
@Schema(description = "Info about a pattern fetched from a remote URL")
public record RemotePattern(@Schema(description = "Name of the pattern") String name,
                            @Schema(description = "URL from which the pattern has been fetched") String url) {
}
