package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Issue;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for {@link Issue} data.
 */
public interface IssueRepository extends CrudRepository<Issue, ObjectId>, QueryDslPredicateExecutor<Issue> {

    /**
     * Finds the {@link Issue} with the given revision number for a specific
     * {@link com.capitalone.dashboard.model.CollectorItem}.
     *
     * @param collectorItemId collector item id
     * @param revisionNumber revision number
     * @return a {@link Issue}
     */
    Issue findByCollectorItemIdAndScmRevisionNumber(ObjectId collectorItemId, String revisionNumber);

    @Query(value="{ 'collectorItemId': ?0, 'scmCommitTimestamp': { $gt: ?1 }}")
    List<Issue> findByCollectorItemIdAndScmCommitTimestamp(ObjectId collectorItemid, Long scmCommitTimestampThreshold);

    Issue findByCollectorItemIdAndNumber(ObjectId collectorItemId, String number);
    Issue findByOrgNameAndRepoNameAndNumber(String orgName, String repoName, String number);
}