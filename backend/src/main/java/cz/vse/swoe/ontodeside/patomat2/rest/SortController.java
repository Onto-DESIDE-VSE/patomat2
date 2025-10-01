package cz.vse.swoe.ontodeside.patomat2.rest;

import cz.vse.swoe.ontodeside.patomat2.model.PatternInstance;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Tag(name = "Pattern matches sorting", description = "API for sorting pattern matches")
@RestController
@RequestMapping("/sort")
public class SortController {

    public record SortMethodDto(String method, String name, Boolean includeSparql) {}

    public enum SortMethod {
        DEFAULT("dummy", "Dummy - no sort", false),
        RANDOM("random", "Random", false);

        private final String value;
        private final String name;
        private final Boolean includeSparql;

        SortMethod(String value, String name, Boolean includeSparql) {
            this.value = value;
            this.name = name;
            this.includeSparql = includeSparql;
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public Boolean getIncludeSparql() {
            return includeSparql;
        }
    }

    @Operation(summary = "Get available sorting methods")
    @ApiResponse(responseCode = "200", description = "List of available sorting methods")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SortMethodDto> getSortMethods() {
        return Arrays.stream(SortMethod.values())
                     .map(method -> new SortMethodDto(
                             method.getValue(),
                             method.getName(),
                             method.getIncludeSparql()
                     ))
                     .toList();
    }

    //TODO replace dummy sort with something more useful
    @Operation(summary = "Sort pattern matches - default order")
    @ApiResponse(responseCode = "200", description = "Pattern matches in default order")
    @PostMapping(value = "/dummy", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PatternInstance>> sortDummy(
            @Parameter(description = "List of pattern instances to sort")
            @RequestBody List<PatternInstance> patternInstances) {
        return ResponseEntity.ok(new ArrayList<>(patternInstances));
    }

    @Operation(summary = "Sort pattern matches - random order")
    @ApiResponse(responseCode = "200", description = "Pattern matches in random order")
    @PostMapping(value = "/random", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PatternInstance>> sortRandom(
            @Parameter(description = "List of pattern instances to sort")
            @RequestBody List<PatternInstance> patternInstances) {
        List<PatternInstance> result = new ArrayList<>(patternInstances);
        Collections.shuffle(result);
        return ResponseEntity.ok(result);
    }
}
