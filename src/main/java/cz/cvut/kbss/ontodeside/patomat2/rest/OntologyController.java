package cz.cvut.kbss.ontodeside.patomat2.rest;

import cz.cvut.kbss.ontodeside.patomat2.service.OntologyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
}
