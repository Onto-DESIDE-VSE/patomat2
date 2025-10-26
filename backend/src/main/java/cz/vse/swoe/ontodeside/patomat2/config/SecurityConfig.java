package cz.vse.swoe.ontodeside.patomat2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${patomat2.log-viewer.username:admin}")
    private String logUsername;

    @Value("${patomat2.log-viewer.password:admin}")
    private String logPassword;

    private final ApplicationConfig config;

    private final InvalidSessionTracker invalidSessionTracker;

    private final ObjectMapper objectMapper;

    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(ApplicationConfig config, InvalidSessionTracker invalidSessionTracker,
                          ObjectMapper objectMapper, PasswordEncoder passwordEncoder) {
        this.config = config;
        this.invalidSessionTracker = invalidSessionTracker;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                           .requestMatchers("/logs/**").authenticated()
                           .anyRequest().permitAll()
                   )
                   .anonymous(auth -> auth.authenticationFilter(new SessionCountingAnonymousFilter("NotImportant", invalidSessionTracker, objectMapper)))
                   .httpBasic(Customizer.withDefaults())
                   .cors(cc -> cc.configurationSource(createCorsConfiguration()))
                   .csrf(AbstractHttpConfigurer::disable)
                   .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)).build();
    }

    private CorsConfigurationSource createCorsConfiguration() {
        // Since we are using cookie-based sessions, we have to specify the URL of the clients (CORS allowed origins)
        final CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        // This is the frontend development server URL
        corsConfiguration.setAllowedOrigins(List.of(config.getCorsAllowedOrigins().split(",")));
        corsConfiguration.addExposedHeader(HttpHeaders.AUTHORIZATION);
        corsConfiguration.addExposedHeader(HttpHeaders.LOCATION);
        corsConfiguration.addExposedHeader(HttpHeaders.CONTENT_DISPOSITION);
        corsConfiguration.setAllowCredentials(true);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                                .username(logUsername)
                                .password(passwordEncoder.encode(logPassword))
                                .roles("ADMIN")
                                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}
