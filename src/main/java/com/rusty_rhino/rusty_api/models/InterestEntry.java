package com.rusty_rhino.rusty_api.models;

import org.springframework.data.annotation.Id;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.GeneratedValue;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;

import lombok.Data;

@Container(containerName = "interest", autoCreateContainer = false)
@Data
public class InterestEntry {

    @Id
    @GeneratedValue
    private String id;

    private String name;

    private String email;

    private String phone;

    private String message;

    private String inventoryItemId;

    private long createdAt;

    @PartitionKey
    private String pk = "interest";
}
