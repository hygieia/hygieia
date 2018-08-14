package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.util.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Component
public class RestOperationsSupplier implements Supplier<RestOperations> {

    @Autowired
    GitHubSettings settings;

    @Override
    public RestOperations get() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(settings.getConnectTimeout());
        requestFactory.setReadTimeout(settings.getReadTimeout());
        return new RestTemplate(requestFactory);
    }
}