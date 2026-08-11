package com.jayowiee.urlshortener.service;

import com.jayowiee.urlshortener.model.UrlMapping;
import com.jayowiee.urlshortener.repository.UrlMappingRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Optional;

@Service
public class UrlShortenerService {

    private final UrlMappingRepository repository;

    public UrlShortenerService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    public UrlMapping shortenUrl(String originalUrl) {
        // Check if URL already shortened
        Optional<UrlMapping> existing = repository.findAll()
            .stream()
            .filter(u -> u.getOriginalUrl().equals(originalUrl))
            .findFirst();

        if (existing.isPresent()) return existing.get();

        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl(originalUrl);
        mapping.setShortCode(UUID.randomUUID().toString().substring(0, 7));
        return repository.save(mapping);
    }

    public Optional<UrlMapping> getByCode(String code) {
        Optional<UrlMapping> mapping = repository.findByShortCode(code);
        mapping.ifPresent(m -> {
            m.setClickCount(m.getClickCount() + 1);
            repository.save(m);
        });
        return mapping;
    }

    public java.util.List<UrlMapping> getAll() {
        return repository.findAll();
    }
}