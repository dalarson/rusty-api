package com.rusty_rhino.rusty_api.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;

import io.netty.handler.codec.http.HttpContentEncoder.Result;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@AllArgsConstructor
public class InventoryImageController {
    private final BlobServiceClient blobServiceClient;
    private final String containerName = "img";

    @PostMapping("/img")
    public String uploadImage(@RequestParam MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(this.containerName);
            String filename = file.getOriginalFilename();
            // hash the file name so there aren't conflicts
            String newImgName = UUID.randomUUID().toString() + filename.substring(filename.lastIndexOf("."));
            BlobClient blobClient = containerClient.getBlobClient(newImgName);
            blobClient.upload(inputStream, file.getSize(), true);
            return blobClient.getBlobUrl();
        }
    }

}
