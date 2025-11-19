package cz.vse.swoe.ontodeside.patomat2.service.pattern;

import cz.vse.swoe.ontodeside.patomat2.exception.ResourceFetchException;
import cz.vse.swoe.ontodeside.patomat2.model.Pattern;
import cz.vse.swoe.ontodeside.patomat2.model.RemotePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Cache for predefined patterns specified in configuration.
 */
@Service
public class PatternCache {

    private static final Logger LOG = LoggerFactory.getLogger(PatternCache.class);

    private final PatternParser patternParser;

    // Map of URL -> pattern
    private final Map<String, Pattern> patterns = new HashMap<>();

    public PatternCache(PatternParser patternParser) {
        this.patternParser = patternParser;
    }

    /**
     * Loads patterns into the cache from the specified URLs.
     * <p>
     * Should be called only once after application startup.
     *
     * @param urls URLs from which to download patterns
     */
    @Async
    public void loadPatterns(@NonNull List<String> urls) {
        Objects.requireNonNull(urls);
        urls.forEach(url -> {
            LOG.debug("Loading pattern from '{}'.", url);
            try {
                final InputStream patternData = readPatternData(url);
                final Pattern pattern = patternParser.readPattern(patternData);
                patterns.put(url, pattern);
            } catch (ResourceFetchException e) {
                LOG.error("Unable to load pattern from {}.", url, e);
                // Do nothing, just skip the url
            }
        });
    }

    private InputStream readPatternData(String url) {
        try {
            final WebClient webClient = WebClient.builder()
                                                 .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                                                                                                           .compress(true)
                                                                                                           .followRedirect(true)))
                                                 .baseUrl(url).build();
            final Flux<DataBuffer> flux = webClient.get()
                                                   .retrieve()
                                                   .bodyToFlux(DataBuffer.class);
            flux.subscribe(DataBufferUtils.releaseConsumer());
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataBufferUtils.write(flux, bos).blockLast();
            return new ByteArrayInputStream(bos.toByteArray());
        } catch (WebClientResponseException e) {
            LOG.error("Failed to download file from {}.", url, e);
            throw new ResourceFetchException("Unable to fetch file from '" + url + "'. Got status " + e.getStatusCode());
        }
    }

    /**
     * Gets a cached pattern with the specified URL.
     *
     * @param url Pattern source URL
     * @return Optional matching pattern, empty when there is no such pattern in the cache
     */
    public Optional<Pattern> getPattern(String url) {
        return Optional.ofNullable(patterns.get(url));
    }

    /**
     * Gets available cached patterns.
     *
     * @return List of remote pattern info instances
     */
    public List<RemotePattern> getAvailablePatterns() {
        return patterns.entrySet().stream().map(e -> new RemotePattern(e.getValue().name(), e.getKey())).toList();
    }
}
