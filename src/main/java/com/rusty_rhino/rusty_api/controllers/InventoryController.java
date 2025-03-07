package com.rusty_rhino.rusty_api.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.azure.cosmos.models.CosmosPatchOperations;
import com.azure.cosmos.models.PartitionKey;
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
    private final PartitionKey pk = new PartitionKey("inventory");

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

    @PatchMapping("/inventory/{id}")
    public String updateInventoryItem(@PathVariable String id, @RequestBody InventoryItem item) {
        try {
            CosmosPatchOperations patchOps = CosmosPatchOperations.create()
                    .replace("/name", item.getName())
                    .replace("/description", item.getDescription())
                    .replace("/price", item.getPrice())
                    .replace("/sold", item.isSold())
                    .replace("/type", item.getType());
            repo.save(id, pk, InventoryItem.class, patchOps).subscribe();
            return item.getId();
        } catch (Exception e) {
            String msg = e.getMessage();
            log.error(msg, e);
            return msg;
        }
    }

    @DeleteMapping("/inventory/{id}")
    public void deleteInventoryItem(@PathVariable String id) {
        try {
            repo.deleteById(id, pk).subscribe();
        } catch (Exception e) {
            String msg = e.getMessage();
            log.error(msg, e);
        }
    }

}
