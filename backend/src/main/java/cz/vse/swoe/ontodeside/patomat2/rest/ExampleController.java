package cz.vse.swoe.ontodeside.patomat2.rest;

import cz.vse.swoe.ontodeside.patomat2.service.ExampleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Example", description = "API to view example transformation")
@RestController
@RequestMapping("/examples")
public class ExampleController {

    private final ExampleService service;

    public ExampleController(ExampleService service) {this.service = service;}

    @Operation(summary = "Get available transformation examples")
    @ApiResponse(responseCode = "200", description = "Transformation example names")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getExamples() {
        return service.getExamples();
    }

    @Operation(summary = "Loads example ontology and patterns")
    @ApiResponse(responseCode = "200", description = "Example loaded")
    @ApiResponse(responseCode = "404", description = "No such example exists")
    @PostMapping
    public ResponseEntity<Void> loadExamples(@Parameter(description = "Name of the example")
                                             @RequestParam String name) {
        service.loadExample(name);
        return ResponseEntity.ok().build();
    }
}
