package cz.vse.swoe.ontodeside.patomat2.rest;

import cz.vse.swoe.ontodeside.patomat2.service.sort.Sort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

@Tag(name = "Pattern matches sorting", description = "API for sorting pattern matches")
@RestController
@RequestMapping("/sort")
public class SortController {

    @Operation(summary = "Get available sorting methods")
    @ApiResponse(responseCode = "200", description = "List of available sorting methods")
    @GetMapping(value = "/options", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SortDto> getSortMethods() {
        return Stream.of(Sort.values()).map(SortDto::new).toList();
    }

    public record SortDto(String name, String value){
        public SortDto(Sort sort){
            this(sort.getName(), sort.getValue());
        }
    }
}
