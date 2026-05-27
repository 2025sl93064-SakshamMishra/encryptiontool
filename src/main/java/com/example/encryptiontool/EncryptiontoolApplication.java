package com.example.encryptiontool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EncryptiontoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(EncryptiontoolApplication.class, args);
	}

}
