package com.capitalone.dashboard.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetails;

import com.capitalone.dashboard.auth.standard.StandardUserDetails;
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
	
	public static AuthType getAuthTypeByPrincipal(Object principal) {
		if(principal instanceof LdapUserDetails) {
			return AuthType.LDAP;
		}
		
		if(principal instanceof StandardUserDetails) {
			return AuthType.STANDARD;
		}
		
		throw new RuntimeException("AuthType is Unknown");
	}
	
	private static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
	
}
