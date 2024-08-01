package cz.cvut.kbss.ontodeside.patomat2.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.ontodeside.patomat2.rest.handler.ErrorInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import java.io.IOException;

public class SessionCountingAnonymousFilter extends AnonymousAuthenticationFilter {

    private final InvalidSessionTracker invalidSessionTracker;

    private final ObjectMapper objectMapper;

    public SessionCountingAnonymousFilter(String key, InvalidSessionTracker invalidSessionTracker,
                                          ObjectMapper objectMapper) {
        super(key);
        this.invalidSessionTracker = invalidSessionTracker;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {
        try {
            super.doFilter(req, res, chain);
        } catch (SessionAuthenticationException e) {
            final HttpServletResponse response = (HttpServletResponse) res;
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            objectMapper.writeValue(response.getOutputStream(),
                    new ErrorInfo(((HttpServletRequest) req).getRequestURI(), "Too many open sessions. Please try again later."));
        }
    }

    @Override
    protected Authentication createAuthentication(HttpServletRequest request) {
        if (invalidSessionTracker.containsSession(request.getSession().getId())) {
            throw new SessionAuthenticationException("Too many open sessions. Please try again later.");
        }
        return super.createAuthentication(request);
    }
}
