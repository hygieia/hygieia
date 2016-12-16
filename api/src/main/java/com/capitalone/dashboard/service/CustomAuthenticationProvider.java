package com.capitalone.dashboard.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.AuthenticatedUser;
import com.capitalone.dashboard.repository.AuthenticationRepository;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private AuthenticationRepository authenticationRepository;

	@Autowired
	public void setAuthenticationRepository(AuthenticationRepository authenticationRepository) {
		this.authenticationRepository = authenticationRepository;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		com.capitalone.dashboard.model.Authentication dbUser = authenticationRepository.findByUsername(authentication.getName());

		 if (dbUser != null && dbUser.checkPassword(authentication.getCredentials().toString())) {
			 List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
			 grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
			 return new AuthenticatedUser(dbUser.getUsername(), grantedAuths);
		 }
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
