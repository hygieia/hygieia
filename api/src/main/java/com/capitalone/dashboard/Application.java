package com.capitalone.dashboard;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

import com.capitalone.dashboard.config.MongoConfig;
import com.capitalone.dashboard.config.RestApiAppConfig;
import com.capitalone.dashboard.config.WebMVCConfig;

/**
 * Application configuration and bootstrap
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class, RestApiAppConfig.class, WebMVCConfig.class, MongoConfig.class);
    }

    public static void main(String[] args) {
        new Application().configure(new SpringApplicationBuilder(Application.class)).run(args);
    }
}
