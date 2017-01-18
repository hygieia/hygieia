package com.capitalone.dashboard.auth.ldap;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.service.UserInfoService;

@Component
public class AuthoritiesPopulator implements LdapAuthoritiesPopulator {
	
	private final UserInfoService userInfoService;
	
	@Autowired
	public AuthoritiesPopulator(UserInfoService userInfoService) {
		this.userInfoService = userInfoService;
	}

	@Override
	public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData,
			String username) {
		Collection<UserRole> roles = userInfoService.getAuthorities(username, AuthType.LDAP);
		return createAuthorities(roles);
	}
	
	private Collection<? extends GrantedAuthority> createAuthorities(Collection<UserRole> authorities) {
		Collection<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
		authorities.forEach(authority -> {
			grantedAuthorities.add(new SimpleGrantedAuthority(authority.name())); 
		});
		
		return grantedAuthorities;
	}

}
