package cz.vse.swoe.ontodeside.patomat2.rest;

import cz.vse.swoe.ontodeside.patomat2.model.PatternInstance;
import cz.vse.swoe.ontodeside.patomat2.service.ExampleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Example", description = "API to view example transformation")
@RestController
@RequestMapping("/example")
public class ExampleController {

    private final ExampleService service;

    public ExampleController(ExampleService service) {this.service = service;}

    @Operation(summary = "Check if there is an example transformation input available")
    @ApiResponse(responseCode = "200", description = "Transformation example is available")
    @ApiResponse(responseCode = "404", description = "Transformation example is not available")
    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity<Void> hasExample() {
        if (service.hasExample()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get matches of the example patterns in the example ontology")
    @ApiResponse(responseCode = "200", description = "List of pattern matches (instances)")
    @ApiResponse(responseCode = "409", description = "Ontology has not been uploaded")
    @GetMapping("/matches")
    public List<PatternInstance> getMatches() {
        // TODO
        return null;
    }
}
