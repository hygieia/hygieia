package com.capitalone.dashboard.service;

import java.util.Collection;

import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.UserRole;

public interface UserInfoService {

	public Collection<UserRole> getAuthorities(String username);
	public UserInfo getUserInfo(String username);
	
}
