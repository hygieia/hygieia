package com.capitalone.dashboard.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.capitalone.dashboard.model.AuthType;

public class AuthenticationUtil {

	public static final String AUTH_TYPE = "auth_type";
	
	public static String getUsernameFromContext() {
		Authentication authentication = getAuthentication();
		if (authentication != null) {
			return authentication.getName();
		}
		
		return null;
	}
	
	public static AuthType getAuthTypeFromContext() {
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
