package com.antigravity.diabetes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DiabetesManageSysApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiabetesManageSysApplication.class, args);
	}

}

