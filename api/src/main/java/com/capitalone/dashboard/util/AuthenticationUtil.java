package com.capitalone.dashboard.util;

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
		return AuthType.valueOf((String)SecurityContextHolder.getContext().getAuthentication().getDetails());
	}
	
}
