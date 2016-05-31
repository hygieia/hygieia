package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.CloudSubNetwork;
import com.capitalone.dashboard.model.NameValue;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface CloudSubNetworkRepository extends
        CrudRepository<CloudSubNetwork, ObjectId>,
        QueryDslPredicateExecutor<CloudInstance> {

    @Query(value = "{ collectorItemId:  ?0 }")
    CloudSubNetwork findByCollectorItemId(ObjectId collectorItemId);

    @Query(value= "{ tags: ?0 }")
    Collection<CloudSubNetwork> findByTags(List<NameValue> tags);


    @Query(value = "{subnetId : ?0}")
    CloudSubNetwork findBySubnetId(String subnetId);

    @Query(value = "{ 'tags.name' : ?0, 'tags.value' : ?1 }")
    Collection<CloudSubNetwork> findByTagNameAndValue(String name, String value);

    Collection<CloudSubNetwork> findBySubnetIdIn(List<String> subnetId);

    Collection<CloudSubNetwork> findByAccountNumber(String accountNumber);

}
