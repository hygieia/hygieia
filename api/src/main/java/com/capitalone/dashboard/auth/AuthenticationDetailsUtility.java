package com.capitalone.dashboard.auth;

import java.util.HashMap;
import java.util.Map;

import com.capitalone.dashboard.model.AuthType;

public class AuthenticationDetailsUtility {
	
	private static final String AUTH_TYPE = "auth_type";
	
	public static Map<Object, Object> createDetails(AuthType authType) {
		Map<Object, Object> details = new HashMap<>();
		details.put(AUTH_TYPE, authType);
		return details;
	}
	
}
