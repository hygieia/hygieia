package com.capitalone.dashboard.auth.ping;

import java.util.Map;

import org.springframework.security.core.Authentication;

public interface PingAuthenticationService {

	Authentication getAuthenticationFromHeaders(Map<String, String> requestHeadersMap);
}
