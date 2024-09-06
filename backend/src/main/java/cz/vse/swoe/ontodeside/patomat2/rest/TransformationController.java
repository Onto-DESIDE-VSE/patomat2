package cz.vse.swoe.ontodeside.patomat2.rest;

import cz.vse.swoe.ontodeside.patomat2.model.TransformationSpecification;
import cz.vse.swoe.ontodeside.patomat2.model.TransformationSummary;
import cz.vse.swoe.ontodeside.patomat2.service.TransformationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ontology transformation", description = "Ontology transformation API")
@RestController
@RequestMapping("/transformation")
public class TransformationController {

    private final TransformationService transformationService;

    public TransformationController(TransformationService transformationService) {
        this.transformationService = transformationService;
    }

    @Operation(summary = "Transform ontology using the specified transformation pattern instances")
    @ApiResponse(responseCode = "200", description = "Summary of the performed transformation")
    @ApiResponse(responseCode = "409", description = "Ontology has not been uploaded")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TransformationSummary transform(@Parameter(
            description = "Transformation pattern instances to apply, possibly including custom new entity labels")
                                           @RequestBody TransformationSpecification transformation) {
        return transformationService.transform(transformation);
    }

    @Operation(summary = "Download the transformed ontology")
    @ApiResponse(responseCode = "200", description = "Transformed ontology")
    @ApiResponse(responseCode = "409",
                 description = "Ontology has not been uploaded or transformation has not been performed")
    @GetMapping(value = "/ontology/content")
    public ResponseEntity<Resource> getTransformedOntology() {
        return OntologyStoringController.buildResponseWithAttachment(transformationService.getTransformedOntology());
    }
}
