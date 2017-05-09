package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Pull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for {@link Pull} data.
 */
public interface PullRepository extends CrudRepository<Pull, ObjectId>, QueryDslPredicateExecutor<Pull> {

    /**
     * Finds the {@link Pull} with the given revision number for a specific
     * {@link com.capitalone.dashboard.model.CollectorItem}.
     *
     * @param collectorItemId collector item id
     * @param revisionNumber revision number
     * @return a {@link Pull}
     */
    Pull findByCollectorItemIdAndScmRevisionNumber(ObjectId collectorItemId, String revisionNumber);

    @Query(value="{ 'collectorItemId': ?0, 'scmCommitTimestamp': { $gt: ?1 }}")
    List<Pull> findByCollectorItemIdAndScmCommitTimestamp(ObjectId collectorItemid,
                                                          Long scmCommitTimestampThreshold);

    Pull findByCollectorItemIdAndNumber(ObjectId collectorItemId, String number);
    Pull findByOrgNameAndRepoNameAndNumber(String orgName, String repoName, String number);

}
