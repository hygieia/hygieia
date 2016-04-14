package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.GerritRepo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface GerritRepoRepository extends BaseCollectorItemRepository<GerritRepo> {

    @Query(value="{ 'collectorId' : ?0, options.repoUrl : ?1, options.branch : ?2}")
    GerritRepo findGitHubRepo(ObjectId collectorId, String url, String branch);

    @Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<GerritRepo> findEnabledGitHubRepos(ObjectId collectorId);
}
