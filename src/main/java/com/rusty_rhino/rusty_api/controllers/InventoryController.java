package com.rusty_rhino.rusty_api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.rusty_rhino.rusty_api.models.InventoryItem;
import com.rusty_rhino.rusty_api.repository.InventoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RestController
@AllArgsConstructor
public class InventoryController {
    private final InventoryRepository repo;

    @GetMapping("/inventory")
    Flux<InventoryItem> getInventory() {
        return repo.findAll();
    }

    @PutMapping("/inventory")
    public String insertInventoryItem(@RequestBody InventoryItem item) {
        try {
            repo.save(item).subscribe();
            return item.getId();
        } catch (Exception e) {
            String msg = e.getMessage();
            log.error(msg, e);
            return msg;
        }
    }

}
