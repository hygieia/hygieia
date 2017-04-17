package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserInfo;

public interface UserInfoRepository extends CrudRepository<UserInfo, ObjectId>{

	UserInfo findByUsernameAndAuthType(String username, AuthType authType);
	
}
