package cz.cvut.kbss.ontodeside.patomat2.config;

import cz.cvut.kbss.ontodeside.patomat2.service.FileStorageService;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> sessionListener(ApplicationConfig config,
                                                                                InvalidSessionTracker invalidSessionTracker,
                                                                                FileStorageService fileStorageService) {
        return new ServletListenerRegistrationBean<>(new SessionCleanupListener(config, invalidSessionTracker, fileStorageService));
    }
}
