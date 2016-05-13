package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.CloudVolumeStorage;
import com.capitalone.dashboard.model.NameValue;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface CloudVolumeRepository extends
        CrudRepository<CloudVolumeStorage, ObjectId>,
        QueryDslPredicateExecutor<CloudVolumeStorage> {

    @Query(value = "{ collectorItemId:  ?0 }")
    Collection<CloudVolumeStorage> findByCollectorItemId(ObjectId collectorItemId);

    @Query(value = "{volumeId : ?0}")
    CloudVolumeStorage findByVolumeId(String volumeId);

    @Query(value = "{ tags: ?0 }")
    Collection<CloudVolumeStorage> findByTags(List<NameValue> tags);

    @Query(value = "{ 'tags.name' : ?0, 'tags.value' : ?1 }")
    Collection<CloudVolumeStorage> findByTagNameAndValue(String name, String value);

    Collection<CloudVolumeStorage> findByVolumeIdIn(List<String> volumeId);

    Collection<CloudVolumeStorage> findByAttachInstancesIn(List<String> attachInstances);

    @Query(value = "{accountNumber : ?0}")
    Collection<CloudVolumeStorage> findByAccountNumber(String accountNumber);

}
