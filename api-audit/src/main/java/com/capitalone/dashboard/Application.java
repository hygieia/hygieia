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
import springfox.documentation.swagger.web.UiConfiguration;
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
                .apis(RequestHandlerSelectors.any())
                .paths(regex("/.*"))
                .build()
                .pathMapping("/")
                .apiInfo(metadata());
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfiguration.DEFAULT;
    }

    private ApiInfo metadata() {
        return new ApiInfoBuilder()
                .title("Hygieia Apiaudit")
                .description("Apiaudit Documentation for Hygieia")
                .version("2.0")
                .contact(new Contact("Tapabrata Pal", "https://github.com/capitalone/Hygieia", "hygieia@capitalone.com"))
                .build();
    }
}
