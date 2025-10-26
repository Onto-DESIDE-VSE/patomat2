package cz.vse.swoe.ontodeside.patomat2.rest;

import cz.vse.swoe.ontodeside.patomat2.service.LogViewerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Logs", description = "Provides access to application logs")
@RestController
@RequestMapping("/logs")
public class LogViewerController {

    private final LogViewerService service;

    public LogViewerController(LogViewerService service) {this.service = service;}

    @Operation(description = "Gets the complete application log")
    @ApiResponse(responseCode = "200", description = "Log string")
    @GetMapping(value = "/application", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getApplicationLog() {
        return service.getApplicationLog();
    }
}
