package com.ivodam.finalpaper.edast;

import com.ivodam.finalpaper.edast.service.FileStorageService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class EdastApplication implements CommandLineRunner {

	@Resource
	FileStorageService fileStorageService;
	public static void main(String[] args) {
		SpringApplication.run(EdastApplication.class, args);
	}

	public void run(String... args) throws Exception {
		//fileStorageService.deleteAll();
		fileStorageService.init();
	}
}
