package com.capitalone.dashboard.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public interface UserInfoService {

	Collection<? extends GrantedAuthority> getAuthorities(String username);

}
