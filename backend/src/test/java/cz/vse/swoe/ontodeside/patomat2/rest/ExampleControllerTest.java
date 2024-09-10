package cz.vse.swoe.ontodeside.patomat2.rest;

import cz.vse.swoe.ontodeside.patomat2.service.ExampleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
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
    void hasExampleReturnsNotFoundWhenThereIsNotExampleConfigured() throws Exception {
        when(service.hasExample()).thenReturn(false);
        mockMvc.perform(head("/example")).andExpect(status().isNotFound());
        verify(service).hasExample();
    }

    @Test
    void hasExampleReturnsOkWhenExampleIsConfigured() throws Exception {
        when(service.hasExample()).thenReturn(true);
        mockMvc.perform(head("/example")).andExpect(status().isOk());
        verify(service).hasExample();
    }
}
