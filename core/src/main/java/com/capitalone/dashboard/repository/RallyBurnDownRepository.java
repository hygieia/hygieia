package com.capitalone.dashboard.repository;

import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

import com.capitalone.dashboard.model.RallyBurnDownData;

public interface RallyBurnDownRepository extends CrudRepository<RallyBurnDownData, ObjectId>{
	
	RallyBurnDownData findByIterationIdAndProjectId(String iterationId, String projectId);
	
}
	
