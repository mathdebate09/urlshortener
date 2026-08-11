package com.jayowiee.urlshortener.controller;

import com.jayowiee.urlshortener.model.UrlMapping;
import com.jayowiee.urlshortener.service.UrlShortenerService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.*;

@RestController
public class UrlShortenerController {

    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    @PostMapping("/shorten")
    public ResponseEntity<Map<String, String>> shorten(@RequestBody Map<String, String> body) {
        String originalUrl = body.get("url");
        if (originalUrl == null || originalUrl.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "URL is required"));
        }

        UrlMapping mapping = service.shortenUrl(originalUrl);
        return ResponseEntity.ok(Map.of(
            "shortCode", mapping.getShortCode(),
            "shortUrl", "http://localhost:8080/" + mapping.getShortCode(),
            "originalUrl", mapping.getOriginalUrl()
        ));
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        return service.getByCode(code)
            .map(m -> ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(m.getOriginalUrl()))
                .<Void>build())
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/urls")
    public ResponseEntity<List<UrlMapping>> getAllUrls() {
        return ResponseEntity.ok(service.getAll());
    }
}