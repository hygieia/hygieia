package com.capitalone.dashboard.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	public Collection<UserRole> getAuthorities(String username) {
		return getUserInfo(username).getAuthorities();
	}
	
	@Override
	public UserInfo getUserInfo(String username) {
		UserInfo userInfo = userInfoRepository.findByUsername(username);
		if(userInfo == null) {
			userInfo = new UserInfo();
			userInfo.setUsername(username);
			userInfo.setAuthorities(Lists.newArrayList(UserRole.ROLE_USER));
			userInfoRepository.save(userInfo);
		}
		
		return userInfo;
	}
	
}
