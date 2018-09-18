package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.AuditStatus;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
public interface AuditStatusRepository extends PagingAndSortingRepository<AuditStatus, ObjectId>{

}
