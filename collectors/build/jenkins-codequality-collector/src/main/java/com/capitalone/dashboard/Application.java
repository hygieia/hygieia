package com.capitalone.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by plv163 on 13/10/2016.
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {

        HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
