package com.rusty_rhino.rusty_api.repository;

import com.azure.spring.data.cosmos.repository.ReactiveCosmosRepository;
import com.rusty_rhino.rusty_api.models.InventoryItem;

public interface InventoryRepository extends ReactiveCosmosRepository<InventoryItem, String> {
}