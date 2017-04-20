package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserInfo;

public interface UserInfoService {

	UserInfo getUserInfo(String username, AuthType authType);
	
}
