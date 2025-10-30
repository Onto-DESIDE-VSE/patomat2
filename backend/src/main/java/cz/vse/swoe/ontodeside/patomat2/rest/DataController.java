package cz.vse.swoe.ontodeside.patomat2.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Data", description = "Data management")
@RestController
@RequestMapping("/data")
public class DataController {

    private static final Logger LOG = LoggerFactory.getLogger(DataController.class);

    @Operation(description = "Clears data corresponding to the current session")
    @ApiResponse(responseCode = "204", description = "Session successfully invalidated")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear(HttpSession session) {
        if (session != null) {
            LOG.debug("Invalidating session due to user request.");
            session.invalidate();
        }
    }
}
