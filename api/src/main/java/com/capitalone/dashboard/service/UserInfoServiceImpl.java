package com.capitalone.dashboard.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class UserInfoServiceImpl implements UserInfoService {

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(String username) {
		return Lists.newArrayList((new SimpleGrantedAuthority("ROLE_USER")));
	}

}
