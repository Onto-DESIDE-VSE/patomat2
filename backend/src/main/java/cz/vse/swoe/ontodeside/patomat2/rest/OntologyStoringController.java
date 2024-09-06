package cz.vse.swoe.ontodeside.patomat2.rest;

import cz.vse.swoe.ontodeside.patomat2.exception.PatOMat2Exception;
import cz.vse.swoe.ontodeside.patomat2.model.LoadedTransformationInput;
import cz.vse.swoe.ontodeside.patomat2.model.TransformationInput;
import cz.vse.swoe.ontodeside.patomat2.service.OntologyStoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Tag(name = "Ontology storage", description = "Ontology storage API")
@RestController
@RequestMapping("/ontology")
public class OntologyStoringController {

    private final OntologyStoringService ontologyStoringService;

    public OntologyStoringController(
            OntologyStoringService ontologyStoringService) {this.ontologyStoringService = ontologyStoringService;}

    @Operation(summary = "Upload ontology and transformation pattern files")
    @ApiResponse(responseCode = "200")
    @PostMapping(value = "/files")
    public void storeTransformationInput(@Parameter(description = "Ontology file")
                                         @RequestParam(value = "ontology") MultipartFile ontology,
                                         @Parameter(description = "Transformation pattern files")
                                         @RequestParam(value = "pattern") List<MultipartFile> patterns) {
        ontologyStoringService.saveOntologyAndPatterns(ontology, patterns);
    }

    @Operation(summary = "Load transformation input from URL")
    @ApiResponse(responseCode = "200")
    @PostMapping("/urls")
    public void storeTransformationInput(
            @Parameter(description = "Transformation input containing URLs of ontology and patterns to load")
            @RequestBody TransformationInput transformationInput) {
        ontologyStoringService.saveOntologyAndPatterns(transformationInput);
    }

    @Operation(summary = "Check if an ontology has been uploaded")
    @ApiResponse(responseCode = "200", description = "Ontology has been uploaded")
    @ApiResponse(responseCode = "404", description = "Ontology has not been uploaded")
    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity<Void> isOntologyUploaded() {
        final Optional<String> uploadedOntology = ontologyStoringService.getUploadedOntologyFileName();
        if (uploadedOntology.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }

    @Operation(summary = "Get information about the uploaded transformation input")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "409", description = "Ontology has not been uploaded")
    @GetMapping
    public LoadedTransformationInput getTransformationInput() {
        return ontologyStoringService.getTransformationInput();
    }

    @Operation(summary = "Download the uploaded ontology file")
    @ApiResponse(responseCode = "200", description = "Ontology file returned as attachment")
    @ApiResponse(responseCode = "409", description = "Ontology has not been uploaded")
    @GetMapping("/content")
    public ResponseEntity<Resource> getOntologyFile() {
        final Resource resource = ontologyStoringService.getOntologyFileContent();
        return buildResponseWithAttachment(resource);
    }

    /**
     * Builds an 200 OK response with the specified resource as attachment.
     *
     * @param resource Resource to attach to response
     * @return ResponseEntity with attachment
     */
    static ResponseEntity<Resource> buildResponseWithAttachment(Resource resource) {
        try {
            return ResponseEntity.ok()
                                 .contentLength(resource.contentLength())
                                 .contentType((MediaType.APPLICATION_OCTET_STREAM))
                                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                                 .body(resource);
        } catch (IOException e) {
            throw new PatOMat2Exception("Unable to download ontology file.", e);
        }
    }
}
