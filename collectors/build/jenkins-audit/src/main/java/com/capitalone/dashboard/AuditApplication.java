package com.capitalone.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class AuditApplication {

    public static void main(String[] args) {

        ApplicationContext applicationContext = SpringApplication.run(AuditApplication.class, args);
    }
}
