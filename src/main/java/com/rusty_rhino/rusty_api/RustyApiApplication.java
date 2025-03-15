package com.rusty_rhino.rusty_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.applicationinsights.attach.ApplicationInsights;

@SpringBootApplication
public class RustyApiApplication {

	public static void main(String[] args) {
		ApplicationInsights.attach();

		// Retrieve the connection string for use with the application.
		String connectStr = System.getenv("SPRING_CLOUD_AZURE_STORAGE_CONNECTION_STRING");

		// Create a BlobServiceClient object using a connection string
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
				.connectionString(connectStr)
				.buildClient();

		SpringApplication.run(RustyApiApplication.class, args);
	}
}
