package com.capitalone.dashboard.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import com.capitalone.dashboard.model.AuthType;

public class AuthenticationUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationUtil.class);
	
	public static final String AUTH_TYPE = "auth_type";
	
	public static String getUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
	
	public static AuthType getAuthType() {
		Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
		
		try {
			Map<String, String> detailMap = (HashMap) details;
			return AuthType.valueOf(detailMap.get(AUTH_TYPE));
		} catch (ClassCastException cce) {
			LOGGER.error("Authentication Details was not set as map.", cce.getMessage());
			return null;
		}
		
	}
	
}
