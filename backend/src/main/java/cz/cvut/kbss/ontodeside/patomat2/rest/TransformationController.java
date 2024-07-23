package cz.cvut.kbss.ontodeside.patomat2.rest;

import cz.cvut.kbss.ontodeside.patomat2.model.TransformationSpecification;
import cz.cvut.kbss.ontodeside.patomat2.service.TransformationService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transformation")
public class TransformationController {

    private final TransformationService transformationService;

    public TransformationController(
            TransformationService transformationService) {this.transformationService = transformationService;}

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> transform(@RequestBody TransformationSpecification transformation) {
        final Resource result = transformationService.transform(transformation);
        return OntologyStoringController.buildResponseWithAttachment(result);
    }
}
