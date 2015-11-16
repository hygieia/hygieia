package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.capitalone.dashboard.model.Authentication;

public interface AuthenticationRepository extends PagingAndSortingRepository<Authentication, ObjectId>{
	
	
	 Authentication findByUsername(String username);


}
