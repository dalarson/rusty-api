package com.rusty_rhino.rusty_api.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RestController
@AllArgsConstructor
public class InventoryImageController {
    private final BlobServiceClient blobServiceClient;
    private final String containerName = "img";

    @PostMapping("/img")
    public ResponseEntity<String> uploadImage(@RequestParam MultipartFile file) {
        log.info("Image upload request received: filename={}, size={} bytes, contentType={}",
                file.getOriginalFilename(), file.getSize(), file.getContentType());
        try (InputStream inputStream = file.getInputStream()) {
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(this.containerName);
            if (!containerClient.exists()) {
                log.info("Blob container '{}' does not exist, creating it", containerName);
                containerClient.create();
            }

            String filename = file.getOriginalFilename();
            String newImgName = UUID.randomUUID().toString() + filename.substring(filename.lastIndexOf("."));
            log.info("Uploading blob: {}", newImgName);

            BlobClient blobClient = containerClient.getBlobClient(newImgName);
            blobClient.upload(inputStream, file.getSize(), true);

            String blobUrl = blobClient.getBlobUrl();
            log.info("Upload successful: {}", blobUrl);
            return ResponseEntity.ok(blobUrl);
        } catch (Exception e) {
            log.error("Image upload failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }
}
