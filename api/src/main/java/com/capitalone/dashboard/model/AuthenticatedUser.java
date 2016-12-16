package com.capitalone.dashboard.model;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;


public class AuthenticatedUser implements Authentication {

	private static final long serialVersionUID = 1L;

	private String name;
	private List<GrantedAuthority> grantedAuths;

    public AuthenticatedUser(String name, List<GrantedAuthority> grantedAuths){
        this.name = name;
        this.grantedAuths = grantedAuths;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuths;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public String getName() {
        return this.name;
    }

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}
}