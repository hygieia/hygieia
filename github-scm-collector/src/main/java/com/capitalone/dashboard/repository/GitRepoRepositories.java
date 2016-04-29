package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.GitHubOrg;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by ltz038 on 4/26/16.
 */
public interface GitRepoRepositories extends BaseCollectorItemRepository<GitHubOrg> {

    @Query(value = "{ 'collectorId' : ?0, options.orgUrl : ?1}")
    GitHubOrg findGitHubRepo(ObjectId collectorId, String url);

    @Query(value = "{ 'collectorId' : ?0, enabled: true}")
    List<GitHubOrg> findEnabledGitHubRepos(ObjectId collectorId);
}

