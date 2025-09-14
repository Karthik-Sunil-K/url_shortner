package com.example.shorturl.service;

import com.example.shorturl.model.UrlMapping;
import com.example.shorturl.repository.UrlMappingRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlMappingService {

    private final UrlMappingRepository repository;
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public UrlMappingService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    // Generate random 4-character short key
    private String generateShortKey() {
        StringBuilder sb = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            int index = RANDOM.nextInt(CHARSET.length());
            sb.append(CHARSET.charAt(index));
        }
        return sb.toString();
    }

    public UrlMapping createShortUrl(String originalUrl) {
        String shortKey;
        // Ensure uniqueness
        do {
            shortKey = generateShortKey();
        } while (repository.findByShortKey(shortKey).isPresent());

        UrlMapping urlMapping = new UrlMapping(
                null,
                0L,
                LocalDateTime.now(),
                null,  // no expiry for now
                originalUrl,
                shortKey
        );

        return repository.save(urlMapping);
    }

    public Optional<UrlMapping> getByShortKey(String shortKey) {
        return repository.findByShortKey(shortKey);
    }

    public long getTotalUrlCreated(){
        return  repository.count();
    }

    public void incrementClickCount(UrlMapping mapping) {
        mapping.setClickCount(mapping.getClickCount() + 1);
        repository.save(mapping);
    }
    public UrlMapping getUrlMapping(String shortKey) {
        return repository.findByShortKey(shortKey)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));
    }
}
