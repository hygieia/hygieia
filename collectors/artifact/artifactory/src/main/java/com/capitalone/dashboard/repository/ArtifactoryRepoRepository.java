package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.ArtifactoryRepo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface ArtifactoryRepoRepository extends BaseCollectorItemRepository<ArtifactoryRepo> {

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, options.repoName : ?2}")
    ArtifactoryRepo findArtifactoryRepo(ObjectId collectorId, String instanceUrl, String repoName);

    @Query(value="{ 'collectorId' : ?0, options.instanceUrl : ?1, enabled: true}")
    List<ArtifactoryRepo> findEnabledArtifactoryRepos(ObjectId collectorId, String instanceUrl);
}
