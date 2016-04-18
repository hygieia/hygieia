package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.CloudVirtualNetwork;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface CloudVirtualNetworkRepository extends
        CrudRepository<CloudVirtualNetwork, ObjectId>,
        QueryDslPredicateExecutor<CloudInstance> {

    @Query(value = "{ collectorItemId:  ?0 }")
    CloudVirtualNetwork findByCollectorItemId(ObjectId collectorItemId);

}
