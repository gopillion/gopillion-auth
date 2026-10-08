package com.gopillion.gopillion_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GopillionAuthApplication {
	@RequestMapping("/login")
	String home() {
		return "Hello World!";
	}
	public static void main(String[] args) {
		SpringApplication.run(GopillionAuthApplication.class, args);
		System.out.println("Welcome to GoPillion : Lets get started");
	}

}
