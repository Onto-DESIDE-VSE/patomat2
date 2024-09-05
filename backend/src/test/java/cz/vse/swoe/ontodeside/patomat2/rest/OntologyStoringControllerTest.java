package cz.vse.swoe.ontodeside.patomat2.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.swoe.ontodeside.patomat2.model.TransformationInput;
import cz.vse.swoe.ontodeside.patomat2.rest.handler.RestExceptionHandler;
import cz.vse.swoe.ontodeside.patomat2.service.OntologyStoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.accept.ContentNegotiationManager;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OntologyStoringControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OntologyStoringService ontologyStoringService;

    @InjectMocks
    private OntologyStoringController sut;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(sut)
                                      .setMessageConverters(new ResourceHttpMessageConverter(), new MappingJackson2HttpMessageConverter(new ObjectMapper()))
                                      .setControllerAdvice(new RestExceptionHandler())
                                      .setContentNegotiationManager(new ContentNegotiationManager()).build();
    }

    @Test
    void storeTransformationInputSavesUploadedFilesWhenProvided() throws Exception {
        final MockMultipartFile ontology = new MockMultipartFile("ontology", "ontology.nt", "application/n-triples", OntologyStoringControllerTest.class.getClassLoader()
                                                                                                                                                        .getResourceAsStream("archivo-partial.nt"));
        final MockMultipartFile pattern = new MockMultipartFile("pattern", "pattern.json", MediaType.APPLICATION_JSON_VALUE, OntologyStoringControllerTest.class.getClassLoader()
                                                                                                                                                                .getResourceAsStream("pattern-example.json"));

        mockMvc.perform(multipart("/ontology/files")
                .file(ontology)
                .file(pattern)).andExpect(status().isOk());
        verify(ontologyStoringService).saveOntologyAndPatterns(ontology, List.of(pattern));
    }

    @Test
    void storeTransformationInputSavesProvidedTransformationInputSoThatItCanBeLoadedFromUrls() throws Exception {
        final TransformationInput input = new TransformationInput();
        input.setOntology("https://www.w3.org/TR/skos-reference/skos.rdf");
        input.setPatterns(List.of("https://raw.githubusercontent.com/Onto-DESIDE-VSE/patomat2/main/backend/src/test/resources/pattern-example.json"));

        mockMvc.perform(post("/ontology/url").content(new ObjectMapper().writeValueAsString(input))
                                             .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
        verify(ontologyStoringService).saveOntologyAndPatterns(input);
    }
}
