package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.GitHubRepo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface GitHubRepoRepository extends BaseCollectorItemRepository<GitHubRepo> {

    @Query(value="{ 'collectorId' : ?0, options.repoUrl : ?1, options.branch : ?2}")
    GitHubRepo findGitHubRepo(ObjectId collectorId, String url, String branch);

    @Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<GitHubRepo> findEnabledGitHubRepos(ObjectId collectorId);
}
