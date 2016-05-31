package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.config.collector.CloudConfig;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AWSConfigRepository extends BaseCollectorItemRepository<CloudConfig> {

    @Query(value="{ 'collectorId' : ?0, options.accessKey : ?1, options.secretKey : ?2}")
    CloudConfig findCloudConfig(ObjectId collectorId, String accessKey, String secretKey);

    @Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<CloudConfig> findEnabledCloudConfig(ObjectId collectorId);
}
