package com.oddsbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OddsbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(OddsbotApplication.class, args);
	}
}
