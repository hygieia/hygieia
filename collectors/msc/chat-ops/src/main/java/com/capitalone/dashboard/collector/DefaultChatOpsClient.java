package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.util.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

/**
 * ChatOpsClient
 */

@Component
public class DefaultChatOpsClient implements ChatOpsClient {
    private final RestOperations restOperations;

    @Autowired
    public DefaultChatOpsClient(Supplier<RestOperations> restOperationsSupplier) {
        this.restOperations = restOperationsSupplier.get();
    }


    @SuppressWarnings("unused")
	private ResponseEntity<String> makeRestCall(String url) {
        return restOperations.exchange(url, HttpMethod.GET, null, String.class);
    }
}


	