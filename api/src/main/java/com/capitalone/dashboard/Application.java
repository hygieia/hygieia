package com.capitalone.dashboard;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import com.capitalone.dashboard.config.MongoConfig;
import com.capitalone.dashboard.config.RestApiAppConfig;
import com.capitalone.dashboard.config.WebMVCConfig;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Application configuration and bootstrap
 */
@SpringBootApplication
@EnableSwagger2
@EnableEncryptableProperties
public class Application extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class, RestApiAppConfig.class, WebMVCConfig.class, MongoConfig.class);
    }
    
    public static void main(String[] args) {
        new Application().configure(new SpringApplicationBuilder(Application.class)).run(args);
    }

    @Bean
    public Docket documentation() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.capitalone.dashboard.rest"))
                .paths(regex("/.*"))
                .build()
                .groupName("default")
                .pathMapping("/")
                .apiInfo(metadata());
    }
    
    @Bean
    public Docket documentationV2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.capitalone.dashboard.v2"))
                .paths(regex("/.*"))
                .build()
                .groupName("v2")
                .pathMapping("/api")
                .apiInfo(metadata());
    }

    private ApiInfo metadata() {
        return new ApiInfoBuilder()
                .title("Hygieia API")
                .description("API Documentation for Hygieia")
                .version("2.0")
                .contact(new Contact("Amit Mawkin/Tapabrata Pal", "https://github.com/capitalone/Hygieia", "hygieia@capitalone.com"))
                .build();
    }
}
