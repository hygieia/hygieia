package com.capitalone.dashboard.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.UserRole;

public interface UserInfoService {

	Collection<? extends GrantedAuthority> getAuthorities(String username, AuthType authType);
	UserInfo getUserInfo(String username, AuthType authType);
	Collection<UserInfo> getUsers();
    UserInfo addAuthorityToUser(AuthType authType, String username, UserRole role);
    UserInfo removeAuthorityFromUser(AuthType authType, String username, UserRole role);
	
}
