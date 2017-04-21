package com.capitalone.dashboard.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.repository.UserInfoRepository;
import com.google.common.collect.Sets;

@Component
public class UserInfoServiceImpl implements UserInfoService {

	private UserInfoRepository userInfoRepository;
	
	@Autowired
	public UserInfoServiceImpl(UserInfoRepository userInfoRepository) {
		this.userInfoRepository = userInfoRepository;
	}
	
	@Override
	public UserInfo getUserInfo(String username, AuthType authType) {
		UserInfo userInfo = userInfoRepository.findByUsernameAndAuthType(username, authType);
		if(userInfo == null) {
			userInfo = createUserInfo(username, authType);
			userInfoRepository.save(userInfo);
		}
		
		return userInfo;
	}
	
    @Override
    public Collection<UserInfo> getUsers() {
        return Sets.newHashSet(userInfoRepository.findAll());
    }
    
	private UserInfo createUserInfo(String username, AuthType authType) {
		UserInfo userInfo = new UserInfo();
		userInfo.setUsername(username);
		userInfo.setAuthType(authType);
		
		return userInfo;
	}
	
}
