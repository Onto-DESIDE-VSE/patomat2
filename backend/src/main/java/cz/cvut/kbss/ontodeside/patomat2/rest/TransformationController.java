package cz.cvut.kbss.ontodeside.patomat2.rest;

import cz.cvut.kbss.ontodeside.patomat2.model.TransformationSpecification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transformation")
public class TransformationController {

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> transform(@RequestBody TransformationSpecification transformation) {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
