package cz.vse.swoe.ontodeside.patomat2.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.vse.swoe.ontodeside.patomat2.rest.handler.RestExceptionHandler;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.accept.ContentNegotiationManager;

public abstract class BaseControllerTestRunner {

    protected MockMvc mockMvc;

    protected void setUp(Object sut) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(sut)
                                      .setMessageConverters(new ResourceHttpMessageConverter(), new MappingJackson2HttpMessageConverter(new ObjectMapper()))
                                      .setControllerAdvice(new RestExceptionHandler())
                                      .setContentNegotiationManager(new ContentNegotiationManager()).build();
    }
}
