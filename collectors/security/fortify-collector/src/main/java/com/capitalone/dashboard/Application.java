package com.capitalone.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@SpringBootApplication
@EnableEncryptableProperties
public class Application {

	public static void main(String[] args) {
    	System.setProperty("jasypt.encryptor.password", "encryptionkey");
		SpringApplication.run(Application.class, args);
	}
}
