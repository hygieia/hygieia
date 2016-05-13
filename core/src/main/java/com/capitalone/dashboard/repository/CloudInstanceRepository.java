package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CloudInstance;
import com.capitalone.dashboard.model.NameValue;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface CloudInstanceRepository extends
        CrudRepository<CloudInstance, ObjectId>,
        QueryDslPredicateExecutor<CloudInstance> {

    @Query(value = "{instanceId : ?0}")
    CloudInstance findByInstanceId(String instanceId);

    @Query(value = "{ tags: ?0 }")
    Collection<CloudInstance> findByTags(List<NameValue> tags);

    @Query(value = "{ 'tags.name' : ?0, 'tags.value' : ?1 }")
    Collection<CloudInstance> findByTagNameAndValue(String name, String value);

    Collection<CloudInstance> findByInstanceIdIn(List<String> instanceId);

    @Query(value = "{accountNumber : ?0}")
    Collection<CloudInstance> findByAccountNumber(String accountNumber);

}
