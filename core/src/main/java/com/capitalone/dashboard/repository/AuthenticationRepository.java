package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Authentication;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AuthenticationRepository extends PagingAndSortingRepository<Authentication, ObjectId>{
	
	
	 Authentication findByUsername(String username);


}
