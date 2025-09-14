package com.example.shorturl.controller;

import com.example.shorturl.model.UrlMapping;
import com.example.shorturl.service.UrlMappingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/url")
public class UrlMappingController {

    private final UrlMappingService service;

    public UrlMappingController(UrlMappingService service) {
        this.service = service;
    }

    // POST: Generate short URL
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createShortUrl(@RequestParam String originalUrl) {
        UrlMapping mapping = service.createShortUrl(originalUrl);
        return ResponseEntity.ok(mapping);
    }

    // GET: Redirect to original URL
    @GetMapping("/{shortKey}")
    public ResponseEntity<?> redirectToOriginal(@PathVariable String shortKey) {
        return service.getByShortKey(shortKey)
                .map(mapping -> {
                    service.incrementClickCount(mapping);
                    return ResponseEntity.status(302)
                            .location(URI.create(mapping.getOriginalUrl()))
                            .build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/total")
    public ResponseEntity<Long> remainingUrlCreation() {
        long total = service.getTotalUrlCreated();
        Long remaining_urls = 14776336 - total;
        return ResponseEntity.ok(remaining_urls);
    }
    @GetMapping("/stats/{shortKey}")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable String shortKey) {
        UrlMapping mapping = service.getUrlMapping(shortKey);

        Map<String, Object> stats = new HashMap<>();
        stats.put("shortKey", mapping.getShortKey());
        stats.put("originalUrl", mapping.getOriginalUrl());
        stats.put("clickCount", mapping.getClickCount());

        return ResponseEntity.ok(stats);
    }
}
