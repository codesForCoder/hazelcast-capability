package com.aniket.movie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MovieMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieMicroserviceApplication.class, args);
	}

}
