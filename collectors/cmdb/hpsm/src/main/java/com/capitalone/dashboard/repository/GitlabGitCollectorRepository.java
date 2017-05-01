package com.capitalone.dashboard.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import com.capitalone.dashboard.model.GitlabGitRepo;

/**
 * Created by benathmane on 20/06/16.
 */
public interface GitlabGitCollectorRepository extends BaseCollectorItemRepository<GitlabGitRepo> {

	@Query(value = "{'collectorId' : ?0, enabled: true}")
    List<GitlabGitRepo> findEnabledGitlabRepos(ObjectId collectorId);
}
