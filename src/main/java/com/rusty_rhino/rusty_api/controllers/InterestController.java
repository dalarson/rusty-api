package com.rusty_rhino.rusty_api.controllers;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rusty_rhino.rusty_api.models.InterestEntry;
import com.rusty_rhino.rusty_api.repository.InterestRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/interest")
public class InterestController {

    private final InterestRepository repo;

    @GetMapping
    public List<InterestEntry> getAllInterest() {
        try {
            return repo.findAll().collectList().block();
        } catch (Exception e) {
            log.warn("Failed to read interest entries: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @PostMapping
    public ResponseEntity<String> submitInterest(@RequestBody InterestEntry entry) {
        if (entry.getName() == null || entry.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Name is required");
        }
        if (entry.getEmail() == null || entry.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (entry.getMessage() != null && entry.getMessage().length() > 250) {
            return ResponseEntity.badRequest().body("Message must be 250 characters or less");
        }

        entry.setCreatedAt(System.currentTimeMillis());
        entry.setPk("interest");

        try {
            InterestEntry saved = repo.save(entry).block();
            log.info("Interest submitted by {} for item {}", saved.getEmail(), saved.getInventoryItemId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());
        } catch (Exception e) {
            log.error("Failed to save interest entry", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save");
        }
    }
}
