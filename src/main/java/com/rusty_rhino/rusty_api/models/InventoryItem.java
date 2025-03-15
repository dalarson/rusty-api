package com.rusty_rhino.rusty_api.models;

import org.springframework.data.annotation.Id;
import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.GeneratedValue;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import io.micrometer.common.lang.NonNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Container(containerName = "inventory")
@Data
@RequiredArgsConstructor
public class InventoryItem {

    @NonNull
    private String name;
    @NonNull
    private String description;
    @Id
    @GeneratedValue
    private String id;
    @NonNull
    private double price;
    private String imgUrl;
    private boolean sold;
    @NonNull
    private String type;
    @PartitionKey
    private String pk = "inventory";

}