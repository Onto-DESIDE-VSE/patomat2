package cz.vse.swoe.ontodeside.patomat2.rest;

import cz.vse.swoe.ontodeside.patomat2.model.PatternInstance;
import cz.vse.swoe.ontodeside.patomat2.service.MatchService;
import cz.vse.swoe.ontodeside.patomat2.service.sort.Sort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Pattern matches", description = "Pattern matches API")
@RestController
@RequestMapping("/matches")
public class PatternMatchesController {

    private final MatchService matchService;

    public PatternMatchesController(MatchService matchService) {this.matchService = matchService;}

    @Operation(summary = "Get matches of uploaded patterns in the uploaded ontology")
    @ApiResponse(responseCode = "200", description = "List of pattern matches (instances)")
    @ApiResponse(responseCode = "409", description = "Ontology has not been uploaded")
    @GetMapping
    public List<PatternInstance> getMatches(@Parameter(description = "Sorting method, optional")
                                            @RequestParam(required = false) String sort) {
        if (sort != null && !sort.isEmpty()) {
            return matchService.findMatches(Sort.fromValue(sort));
        }
        return matchService.findMatches();
    }
}
