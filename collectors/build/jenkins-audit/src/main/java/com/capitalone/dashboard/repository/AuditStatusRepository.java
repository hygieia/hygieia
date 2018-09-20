package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.AuditResult;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AuditStatusRepository extends PagingAndSortingRepository<AuditResult, ObjectId> {

}
