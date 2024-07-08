package cz.cvut.kbss.ontodeside.patomat2.rest;

import cz.cvut.kbss.ontodeside.patomat2.exception.PatOMat2Exception;
import cz.cvut.kbss.ontodeside.patomat2.service.OntologyService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ontology")
public class OntologyController {

    private final OntologyService ontologyService;

    public OntologyController(OntologyService ontologyService) {this.ontologyService = ontologyService;}

    @PostMapping
    public void uploadFiles(@RequestParam("ontology") MultipartFile ontology,
                            @RequestParam("pattern") List<MultipartFile> patterns) {
        ontologyService.saveOntologyAndPatterns(ontology, patterns);
    }

    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity<Void> isOntologyUploaded() {
        final Optional<String> uploadedOntology = ontologyService.getUploadedOntologyFileName();
        if (uploadedOntology.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }

    @GetMapping
    public ResponseEntity<Resource> getOntologyFile() {
        final Resource resource = ontologyService.getOntologyFile();
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
