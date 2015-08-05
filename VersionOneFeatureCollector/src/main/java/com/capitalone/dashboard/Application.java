package com.capitalone.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application configuration and bootstrap
 *
 * @author KFK884
 */
@SpringBootApplication
public class Application {
	/**
	 * Main thread of operation that runs the Spring Boot collector application.
	 *
	 * @param args
	 *            Any command line arguments that need to be captured at runtime
	 *            (currently, none are used)
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
