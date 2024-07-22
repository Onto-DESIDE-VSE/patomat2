package cz.cvut.kbss.ontodeside.patomat2.rest;

import cz.cvut.kbss.ontodeside.patomat2.model.PatternInstance;
import cz.cvut.kbss.ontodeside.patomat2.service.MatchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/matches")
public class PatternMatchesController {

    private final MatchService matchService;

    public PatternMatchesController(MatchService matchService) {this.matchService = matchService;}

    @GetMapping
    public List<PatternInstance> getMatches() {
        return matchService.findMatches();
    }
}
