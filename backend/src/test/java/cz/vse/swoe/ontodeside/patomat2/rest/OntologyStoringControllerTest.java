package cz.vse.swoe.ontodeside.patomat2.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.swoe.ontodeside.patomat2.model.TransformationInput;
import cz.vse.swoe.ontodeside.patomat2.service.OntologyStoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OntologyStoringControllerTest extends BaseControllerTestRunner {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OntologyStoringService ontologyStoringService;

    @InjectMocks
    private OntologyStoringController sut;

    @BeforeEach
    void setUp() {
        super.setUp(sut);
    }

    @Test
    void storeTransformationInputSavesUploadedFilesWhenProvided() throws Exception {
        final MockMultipartFile ontology = new MockMultipartFile("ontology", "ontology.nt",
                "application/n-triples",
                OntologyStoringControllerTest.class.getClassLoader().getResourceAsStream("archivo-partial.nt"));
        final MockMultipartFile pattern = new MockMultipartFile("pattern", "pattern.json",
                MediaType.APPLICATION_JSON_VALUE,
                OntologyStoringControllerTest.class.getClassLoader().getResourceAsStream("pattern-example.json"));
        final TransformationInput emptyInput = new TransformationInput(null, List.of());
        final MockMultipartFile data = new MockMultipartFile("data", "data.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(emptyInput));

        mockMvc.perform(multipart("/ontology")
                       .file(ontology)
                       .file(pattern)
                       .file(data).contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isOk());
        verify(ontologyStoringService).saveOntologyAndPatterns(ontology, List.of(pattern), emptyInput);
    }

    @Test
    void storeTransformationInputSavesTransformationInputWhenNoFilesAreProvided() throws Exception {
        final TransformationInput input = new TransformationInput("https://www.w3.org/TR/skos-reference/skos.rdf",
                List.of("https://raw.githubusercontent.com/Onto-DESIDE-VSE/patomat2/main/backend/src/test/resources/pattern-example.json"));
        final MockMultipartFile data = new MockMultipartFile("data", "data.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(input));

        mockMvc.perform(multipart("/ontology")
                       .file(data).contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isOk());
        verify(ontologyStoringService).saveOntologyAndPatterns(null, List.of(), input);
    }

    @Test
    void storeTransformationInputsSavesTransformationInputAndPatternFiles() throws Exception {
        final MockMultipartFile pattern = new MockMultipartFile("pattern", "pattern.json",
                MediaType.APPLICATION_JSON_VALUE,
                OntologyStoringControllerTest.class.getClassLoader().getResourceAsStream("pattern-example.json"));
        final TransformationInput input = new TransformationInput("https://www.w3.org/TR/skos-reference/skos.rdf",
                List.of("https://raw.githubusercontent.com/Onto-DESIDE-VSE/patomat2/main/backend/src/test/resources/pattern-example.json"));
        final MockMultipartFile data = new MockMultipartFile("data", "data.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(input));

        mockMvc.perform(multipart("/ontology")
                       .file(pattern)
                       .file(data).contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isOk());
        verify(ontologyStoringService).saveOntologyAndPatterns(null, List.of(pattern), input);
    }
}
