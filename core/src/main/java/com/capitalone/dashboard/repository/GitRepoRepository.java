package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRepoData;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for {@link Commit} data.
 */
public interface GitRepoRepository extends CrudRepository<GitRepoData, ObjectId>, QueryDslPredicateExecutor<GitRepoData> {

    @Query(value="{ 'collectorItemId': ?0, 'repoName': { $gt: ?1 }}")
    List<GitRepoData> findByCollectorItemIdAndName(ObjectId collectorItemid, String scmRepoNameThreshold);

}
