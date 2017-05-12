package com.capitalone.dashboard.auth;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import com.capitalone.dashboard.auth.ldap.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.auth.token.TokenAuthenticationService;
import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.service.UserInfoService;

@Component
public class DefaultAuthenticationResponseService implements AuthenticationResponseService {
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	@Autowired
	private UserInfoService userInfoService;
	
	@Override
	public void handle(HttpServletResponse response, Authentication authentication) {
		String firstName = "";
		String middleName = "";
		String lastName = "";
		String displayName = "";
		String emailAddress = "";
		if (authentication.getPrincipal() instanceof CustomUserDetails) {
			firstName = ((CustomUserDetails) authentication.getPrincipal()).getFirstName();
			middleName = ((CustomUserDetails) authentication.getPrincipal()).getMiddleName();
			lastName = ((CustomUserDetails) authentication.getPrincipal()).getLastName();
			displayName = ((CustomUserDetails) authentication.getPrincipal()).getDisplayName();
			emailAddress = ((CustomUserDetails) authentication.getPrincipal()).getEmailAddress();
		}
		Collection<? extends GrantedAuthority> authorities =
				userInfoService.getAuthorities(authentication.getName(), firstName, middleName, lastName, displayName, emailAddress, (AuthType)authentication.getDetails());
		UsernamePasswordAuthenticationToken authenticationWithAuthorities = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), authorities);
		authenticationWithAuthorities.setDetails(authentication.getDetails());
		
		tokenAuthenticationService.addAuthentication(response, authenticationWithAuthorities);

	}

}
