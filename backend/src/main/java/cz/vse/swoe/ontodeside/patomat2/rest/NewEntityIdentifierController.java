package cz.vse.swoe.ontodeside.patomat2.rest;

import cz.vse.swoe.ontodeside.patomat2.model.iri.NewEntityIriConfig;
import cz.vse.swoe.ontodeside.patomat2.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "New entity ID generator", description = "New entity ID generation configuration")
@RestController
@RequestMapping("/new-entity-identifier")
public class NewEntityIdentifierController {

    private final MatchService matchService;

    public NewEntityIdentifierController(MatchService matchService) {
        this.matchService = matchService;
    }

    @Operation(summary = "Get the current configuration of new entity identifier generator")
    @ApiResponse(responseCode = "200", description = "Current configuration of new entity identifier generator")
    @ApiResponse(responseCode = "409", description = "Ontology has not been uploaded")
    @GetMapping(value = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
    public NewEntityIriConfig getNewEntityIriConfig() {
        return matchService.getNewEntityIriConfig();
    }

    @Operation(summary = "Set the configuration of new entity identifier generator")
    @ApiResponse(responseCode = "204",
                 description = "Configuration of new entity identifier generator set successfully")
    @ApiResponse(responseCode = "409", description = "Ontology has not been uploaded")
    @PutMapping(value = "/config", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setNewEntityIriConfig(@RequestBody NewEntityIriConfig newEntityIriConfig) {
        matchService.setNewEntityIriConfig(newEntityIriConfig);
    }
}
