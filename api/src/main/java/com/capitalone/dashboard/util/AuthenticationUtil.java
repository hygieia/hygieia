package com.capitalone.dashboard.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.capitalone.dashboard.model.AuthType;

public class AuthenticationUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationUtil.class);
	
	public static final String AUTH_TYPE = "auth_type";
	
	public static String getUsername() {
		Authentication authentication = getAuthentication();
		if (authentication != null) {
			return authentication.getName();
		}
		
		return null;
	}
	
	public static AuthType getAuthType() {
		Authentication authentication = getAuthentication();
		if (authentication != null) {
			return AuthType.valueOf((String)authentication.getDetails());
		}
		
		return null;
	}
	
	private static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
	
}
