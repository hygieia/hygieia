package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.util.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Supplier that returns an instance of RestOperations
 */
@Component
public class RestOperationsSupplier implements Supplier<RestOperations> {
    @Override
    public RestOperations get() {
        return new RestTemplate();
    }
}
