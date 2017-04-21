package com.capitalone.dashboard.repository;

import java.util.Collection;

import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.capitalone.dashboard.model.Authentication;
import com.capitalone.dashboard.model.UserRole;

public interface AuthenticationRepository extends PagingAndSortingRepository<Authentication, ObjectId>{
	
	
	 Authentication findByUsername(String username);

    Collection<UserRole> findByRolesIn(UserRole roleAdmin);

}
