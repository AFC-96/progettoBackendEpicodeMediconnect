package com.mediconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
// Classe principale: avvia il server Spring Boot e abilita l'esecuzione asincrona dei task

@SpringBootApplication
@EnableAsync
public class MediconnectBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MediconnectBackendApplication.class, args);
	}

}
