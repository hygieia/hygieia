package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.GitlabGitRepo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by benathmane on 20/06/16.
 */
public interface GitlabGitCollectorRepository extends BaseCollectorItemRepository<GitlabGitRepo> {

    @Query(value="{enabled: true}")
    List<GitlabGitRepo> findEnabledGitlabRepos(ObjectId collectorId);
}
