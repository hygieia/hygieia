package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.RequestLog;
import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

public interface RequestLogRepository extends CrudRepository<RequestLog, ObjectId> {
}
