package com.capitalone.dashboard;

import com.capitalone.dashboard.config.LoggingFilter;
import com.capitalone.dashboard.config.MongoConfig;
import com.capitalone.dashboard.config.RestApiAppConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

/**
 * Application configuration and bootstrap
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class, RestApiAppConfig.class, WebMVCConfig.class, MongoConfig.class);
    }

    @Bean
    public Filter loggingFilter(){
        LoggingFilter f = new LoggingFilter();
        return f;
    }
    public static void main(String[] args) {
        new Application().configure(new SpringApplicationBuilder(Application.class)).run(args);
    }
}
