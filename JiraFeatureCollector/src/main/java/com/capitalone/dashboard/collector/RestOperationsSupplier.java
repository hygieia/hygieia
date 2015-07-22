package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.util.Supplier;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Supplier that returns a new {@link RestTemplate}.
 */
@Component
public class RestOperationsSupplier implements Supplier<RestOperations> {
	/**
	 * Handles the REST operation HTTP connection timeout behavior
	 *
	 * @return A configured REST template artifact
	 */
	@Override
	public RestOperations get() {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setConnectTimeout(40000);
		requestFactory.setReadTimeout(40000);

		return new RestTemplate(requestFactory);
	}
}
