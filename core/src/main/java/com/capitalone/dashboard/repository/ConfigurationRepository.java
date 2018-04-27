package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.Configuration;


public interface ConfigurationRepository extends CrudRepository<Configuration, ObjectId> , QueryDslPredicateExecutor<Configuration>{

	Configuration findByCollectorName(String collectorNiceName);
	
}
