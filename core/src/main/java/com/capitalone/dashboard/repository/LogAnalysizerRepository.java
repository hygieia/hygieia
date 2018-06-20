package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.LogAnalysis;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by stevegal on 20/06/2018.
 */
public interface LogAnalysizerRepository extends CrudRepository<LogAnalysis, ObjectId>{
}
