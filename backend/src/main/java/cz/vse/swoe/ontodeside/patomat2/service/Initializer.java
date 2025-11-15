package cz.vse.swoe.ontodeside.patomat2.service;

import cz.vse.swoe.ontodeside.patomat2.config.ApplicationConfig;
import cz.vse.swoe.ontodeside.patomat2.service.pattern.PatternCache;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Initializes the application, performing post-startup tasks.
 */
@Component
public class Initializer implements CommandLineRunner {

    private final ApplicationConfig config;

    private final ApplicationContext context;

    public Initializer(ApplicationConfig config, ApplicationContext context) {
        this.config = config;
        this.context = context;
    }

    @Override
    public void run(String... args) {
        // Initialize pattern cache with pre-configured patterns
        context.getBean(PatternCache.class).loadPatterns(config.getPatterns());
    }
}
