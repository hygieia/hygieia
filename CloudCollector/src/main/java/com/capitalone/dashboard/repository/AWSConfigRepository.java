package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.AWSConfig;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AWSConfigRepository extends BaseCollectorItemRepository<AWSConfig> {

    @Query(value="{ 'collectorId' : ?0, options.accessKey : ?1, options.secretKey : ?2}")
    AWSConfig findAWSConfig(ObjectId collectorId, String accessKey, String secretKey);

    @Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<AWSConfig> findEnabledAWSConfig(ObjectId collectorId);
}
