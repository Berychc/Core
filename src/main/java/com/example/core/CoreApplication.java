package com.example.core;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс приложения, который запускает Spring Boot приложение.
 *
 * Этот класс также настраивает OpenAPI для автоматической генерации
 * документации API.
 */
@OpenAPIDefinition
@SpringBootApplication
public class CoreApplication {

	/**
	 * Точка входа в приложение.
	 *
	 * @param args Аргументы командной строки, переданные приложению.
	 */
	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}
}
