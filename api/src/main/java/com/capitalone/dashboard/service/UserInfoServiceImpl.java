package com.capitalone.dashboard.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.UserRole;
import com.capitalone.dashboard.repository.UserInfoRepository;
import com.google.common.collect.Lists;

@Component
public class UserInfoServiceImpl implements UserInfoService {

	private UserInfoRepository userInfoRepository;
	
	@Autowired
	public UserInfoServiceImpl(UserInfoRepository userInfoRepository) {
		this.userInfoRepository = userInfoRepository;
	}
	
	@Override
	public Collection<UserRole> getAuthorities(String username, AuthType authType) {
		return getUserInfo(username, authType).getAuthorities();
	}
	
	@Override
	public UserInfo getUserInfo(String username, AuthType authType) {
		UserInfo userInfo = userInfoRepository.findByUsernameAndAuthType(username, authType);
		if(userInfo == null) {
			userInfo = createUserInfo(username, authType);
			userInfoRepository.save(userInfo);
		}
		// TODO: this will be refactored
		// basing admin role on admin username
		// plan to have a ui to select new / additional admin
		setAdditionalRoles(userInfo);
		
		return userInfo;
	}

	private UserInfo createUserInfo(String username, AuthType authType) {
		UserInfo userInfo = new UserInfo();
		userInfo.setUsername(username);
		userInfo.setAuthType(authType);
		userInfo.setAuthorities(Lists.newArrayList(UserRole.ROLE_USER));
		
		return userInfo;
	}

	private void setAdditionalRoles(UserInfo userInfo) {
		if ("admin".equals(userInfo.getUsername())) {
			userInfo.getAuthorities().add(UserRole.ROLE_ADMIN);
		}
	}
	
}
