package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.AuditResult;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AuditResultRepository extends PagingAndSortingRepository<AuditResult, ObjectId> {

    AuditResult findById(ObjectId id);
    Iterable<AuditResult> findByDashboardTitle(String dashboardTitle);
}