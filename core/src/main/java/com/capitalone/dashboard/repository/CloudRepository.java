package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Cloud;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface CloudRepository extends
        CrudRepository<Cloud, ObjectId>,
        QueryDslPredicateExecutor<Cloud> {

    @Query(value = "{ collectorItemId:  ?0 }")
    Cloud findByCollectorItemId(ObjectId collectorItemId);

}
