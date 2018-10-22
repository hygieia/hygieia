package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.ArtifactItem;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface ArtifactItemRepository extends BaseCollectorItemRepository<ArtifactItem>{

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, options.repoName : ?2}")
    ArtifactItem findArtifactItem(ObjectId collectorId, String instanceUrl, String repoName);

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
    List<ArtifactItem> findEnabledArtifactItems(ObjectId collectorId, String instanceUrl);

}
