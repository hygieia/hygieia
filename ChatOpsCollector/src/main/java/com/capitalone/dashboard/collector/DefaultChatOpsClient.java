package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

/**
 * GitHubClient implementation that uses SVNKit to fetch information about
 * Subversion repositories.
 */

@Component
public class DefaultChatOpsClient implements ChatOpsClient {
	private static final Log LOG = LogFactory.getLog(DefaultChatOpsClient.class);

	private final ChatOpsSettings settings;

	private final RestOperations restOperations;
	

	@Autowired
	public DefaultChatOpsClient(ChatOpsSettings settings,
			Supplier<RestOperations> restOperationsSupplier) {
		this.settings = settings;
		this.restOperations = restOperationsSupplier.get();
	}

	
	private ResponseEntity<String> makeRestCall(String url) {
		// Not using github auth now. Assuming all public repos.
		return restOperations.exchange(url, HttpMethod.GET, null, String.class);
	}
}
	

	