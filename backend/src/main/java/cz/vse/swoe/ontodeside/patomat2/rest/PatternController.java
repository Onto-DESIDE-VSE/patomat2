package cz.vse.swoe.ontodeside.patomat2.rest;

import cz.vse.swoe.ontodeside.patomat2.model.RemotePattern;
import cz.vse.swoe.ontodeside.patomat2.service.pattern.PatternCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Patterns", description = "Information about patterns")
@RestController
@RequestMapping("/patterns")
public class PatternController {

    private final PatternCache patternCache;

    public PatternController(PatternCache patternCache) {this.patternCache = patternCache;}

    @Operation(summary = "Get available predefined patterns")
    @ApiResponse(responseCode = "200", description = "Available predefined patterns")
    @GetMapping(value = "/predefined", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RemotePattern> getPredefinedPatterns() {
        return patternCache.getAvailablePatterns();
    }
}
