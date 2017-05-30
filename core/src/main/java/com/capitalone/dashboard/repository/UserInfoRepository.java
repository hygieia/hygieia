package com.capitalone.dashboard.repository;

import java.util.Collection;

import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.AuthType;
import com.capitalone.dashboard.model.UserInfo;
import com.capitalone.dashboard.model.UserRole;

public interface UserInfoRepository extends CrudRepository<UserInfo, ObjectId>{

	UserInfo findByUsernameAndAuthType(String username, AuthType authType);

    Collection<UserInfo> findByAuthoritiesIn(UserRole roleAdmin);

    Iterable<UserInfo> findByOrderByUsernameAsc();

}
