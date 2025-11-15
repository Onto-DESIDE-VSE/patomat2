package cz.vse.swoe.ontodeside.patomat2.service.pattern;

import cz.vse.swoe.ontodeside.patomat2.exception.ResourceFetchException;
import cz.vse.swoe.ontodeside.patomat2.model.Pattern;
import cz.vse.swoe.ontodeside.patomat2.model.RemotePattern;
import cz.vse.swoe.ontodeside.patomat2.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
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

    private final FileStorageService storageService;

    private final PatternParser patternParser;

    // Map of URL -> pattern
    private final Map<String, Pattern> patterns = new HashMap<>();

    public PatternCache(FileStorageService storageService, PatternParser patternParser) {
        this.storageService = storageService;
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
                final File patternData = storageService.downloadAndSaveFile(url);
                final Pattern pattern = patternParser.readPattern(patternData);
                patterns.put(url, pattern);
            } catch (ResourceFetchException e) {
                LOG.error("Unable to load pattern from {}.", url, e);
                // Do nothing, just skip the url
            }
        });
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
