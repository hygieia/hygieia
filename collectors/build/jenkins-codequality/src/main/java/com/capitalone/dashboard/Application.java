package com.capitalone.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {

        HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate();
        template.getMessageConverters().forEach(httpMessageConverter ->
        {
            if (httpMessageConverter instanceof  Jaxb2RootElementHttpMessageConverter) {
                ((Jaxb2RootElementHttpMessageConverter)httpMessageConverter).setSupportDtd(true);
            }
        });
        return template;
    }
}
