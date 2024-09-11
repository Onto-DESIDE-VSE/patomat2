package cz.vse.swoe.ontodeside.patomat2.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.swoe.ontodeside.patomat2.service.ExampleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ExampleControllerTest extends BaseControllerTestRunner {

    @Mock
    private ExampleService service;

    @InjectMocks
    private ExampleController sut;

    @BeforeEach
    void setUp() {
        super.setUp(sut);
    }

    @Test
    void getExamplesRetrieveListOfExampleNamesFromService() throws Exception {
        final List<String> exampleNames = List.of("test");
        when(service.getExamples()).thenReturn(exampleNames);
        final MvcResult mvcResult = mockMvc.perform(get("/examples")).andExpect(status().isOk()).andReturn();
        final ObjectMapper objectMapper = new ObjectMapper();
        final List<String> result = objectMapper.readValue(mvcResult.getResponse()
                                                                    .getContentAsString(), new TypeReference<>() {});
        assertEquals(exampleNames, result);
        verify(service).getExamples();
    }

    @Test
    void getExampleMatchesRetrievesMatchesFromService() throws Exception {
        final String exampleName = "test";
        when(service.getExampleMatches(exampleName)).thenReturn(List.of());
        mockMvc.perform(get("/examples/matches").queryParam("name", exampleName)).andExpect(status().isOk());
        verify(service).getExampleMatches(exampleName);
    }
}
