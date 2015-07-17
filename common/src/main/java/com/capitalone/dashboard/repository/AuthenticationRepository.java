package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.capitalone.dashboard.model.Authentication;

public interface AuthenticationRepository extends PagingAndSortingRepository<Authentication, ObjectId>{
	
	
	 Authentication findByUsername(String username);
	 List<Authentication> findByUsernameAndPassword(String username, String password);


}
