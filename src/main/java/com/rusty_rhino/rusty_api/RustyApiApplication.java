package com.rusty_rhino.rusty_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.microsoft.applicationinsights.attach.ApplicationInsights;

@SpringBootApplication
public class RustyApiApplication {

	public static void main(String[] args) {
		ApplicationInsights.attach();
		SpringApplication.run(RustyApiApplication.class, args);
	}
}
