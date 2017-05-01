package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.LibraryPolicyResult;
import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link CodeQuality} data.
 */
public interface LibraryPolicyResultsRepository extends CrudRepository<LibraryPolicyResult, ObjectId>, QueryDslPredicateExecutor<LibraryPolicyResult> {


    LibraryPolicyResult findByCollectorItemIdAndTimestamp(ObjectId collectorItemId, long timestamp);
    LibraryPolicyResult findByCollectorItemId(ObjectId collectorItemId);
}
