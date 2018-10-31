package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.webhook.github.GitHubRepo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

public interface GitHubRepoRepository extends BaseCollectorItemRepository<GitHubRepo>, QueryDslPredicateExecutor<GitHubRepo> {
    @Query(value="{ 'collectorId' : ?0, options.url : ?1, options.branch : ?2}")
    GitHubRepo findGitHubRepo(ObjectId collectorId, String url, String branch);

    @Query(value="{ 'collectorId' : ?0, enabled: true}")
    List<GitHubRepo> findEnabledGitHubRepos(ObjectId collectorId);
}
