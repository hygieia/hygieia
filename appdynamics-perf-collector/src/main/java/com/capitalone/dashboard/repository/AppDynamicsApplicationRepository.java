package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.AppdynamicsApplication;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AppDynamicsApplicationRepository extends BaseCollectorItemRepository<AppdynamicsApplication> {


    AppdynamicsApplication findByCollectorIdAndAppName(ObjectId collectorId, String appName);

    AppdynamicsApplication findByCollectorIdAndAppID(ObjectId collectorId, String appID);

    @Query(value="{ 'collectorId' : ?0, 'enabled': true}")
    List<AppdynamicsApplication> findEnabledAppdynamicsApplications(ObjectId collectorId);

    List<AppdynamicsApplication> findByCollectorIdAndEnabled(ObjectId collectorId, boolean enabled);
}
