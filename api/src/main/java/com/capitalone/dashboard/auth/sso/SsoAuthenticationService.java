package com.capitalone.dashboard.auth.sso;

import java.util.Map;

import org.springframework.security.core.Authentication;

public interface SsoAuthenticationService {

	Authentication getAuthenticationFromHeaders(Map<String, String> requestHeadersMap);
}
