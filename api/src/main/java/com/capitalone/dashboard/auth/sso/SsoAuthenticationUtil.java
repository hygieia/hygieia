package com.capitalone.dashboard.auth.sso;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.auth.AuthProperties;
import com.capitalone.dashboard.auth.ldap.CustomUserDetails;
import com.capitalone.dashboard.model.AuthType;

@Component
public class SsoAuthenticationUtil {
	private static final Logger LOGGER = Logger.getLogger(SsoAuthenticationUtil.class);
	
	@Autowired
	private AuthProperties authProperties;
	
	CustomUserDetails createUser(Map<String, String> userInfo) {
		CustomUserDetails customUserDetails = null;
		
		try {
			if (userInfo.get(authProperties.getUserEid()) != null) {
				customUserDetails = new CustomUserDetails();
				
				customUserDetails.setUsername("" + userInfo.get(authProperties.getUserEid()));
				customUserDetails.setAccountNonExpired(true);
				customUserDetails.setAccountNonLocked(true);
				customUserDetails.setCredentialsNonExpired(true);
				customUserDetails.setEnabled(true);
				customUserDetails.setAuthorities(new ArrayList<GrantedAuthority>());
				
				String userFirstName = userInfo.get(authProperties.getUserFirstName());
				if (userFirstName != null) {
					LOGGER.info("givenName from attr: " + userFirstName);
					customUserDetails.setFirstName("" + userFirstName);
				}

				String userInitials = userInfo.get(authProperties.getUserMiddelInitials());
				if (userInitials != null) {
					LOGGER.info("initials from attr: " + userInitials);
					customUserDetails.setMiddleName("" + userInitials);
				}
				
				String userLastName = userInfo.get(authProperties.getUserLastName());
				if (userLastName != null) {
					LOGGER.info("sn from attr: " + userLastName);
					customUserDetails.setLastName("" + userLastName);
				}
				
				String userDisplayName = userInfo.get(authProperties.getUserDisplayName());
				if (userDisplayName != null) {
					LOGGER.info("displayName from attr: " + userDisplayName);
					customUserDetails.setDisplayName("" + userDisplayName);
				}
				
				String userEmail = userInfo.get(authProperties.getUserEmail());
				if (userEmail != null) {
					LOGGER.info("mail from attr: " + userEmail);
					customUserDetails.setEmailAddress("" + userEmail);
				}
			}
			else {
				LOGGER.error("Authenticated user cannot be loaded");
			}
		} catch (Exception e) {
			LOGGER.error("Exception in mapping user details: " + e);
		}
		return customUserDetails;
	}

	Authentication createSuccessfulAuthentication(CustomUserDetails user) {
		if(user == null) {
			return null;
		}
		PreAuthenticatedAuthenticationToken result = new PreAuthenticatedAuthenticationToken(
				user, null, user.getAuthorities());
		
		// -- SSO Authentication will fetch the user details from LDAP system. The Authentication Type therefore can be given as LDAP.
		result.setDetails(AuthType.LDAP);
		return result;
	}
}
