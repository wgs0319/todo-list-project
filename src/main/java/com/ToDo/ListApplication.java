package com.ToDo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ListApplication {

	public static void main(String[] args) {
		SpringApplication.run(ListApplication.class, args);
	}

}
