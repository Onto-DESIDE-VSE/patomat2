package cz.vse.swoe.ontodeside.patomat2.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Information")
public record PatternInfo(String name, String fileName) {
}
