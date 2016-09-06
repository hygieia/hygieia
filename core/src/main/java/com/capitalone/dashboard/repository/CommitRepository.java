package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Commit;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for {@link Commit} data.
 */
public interface CommitRepository extends CrudRepository<Commit, ObjectId>, QueryDslPredicateExecutor<Commit> {

    /**
     * Finds the {@link Commit} with the given revision number for a specific
     * {@link com.capitalone.dashboard.model.CollectorItem}.
     *
     * @param collectorItemId collector item id
     * @param revisionNumber revision number
     * @return a {@link Commit}
     */
    Commit findByCollectorItemIdAndScmRevisionNumber(ObjectId collectorItemId, String revisionNumber);

    Commit findByScmUrlAndScmBranchAndScmRevisionNumber (String scmUrl, String scmBranch, String scmRevisionNumber);

    List<Commit> findByScmRevisionNumber (String scmUrl);

    @Query(value="{ 'collectorItemId': ?0, 'scmCommitTimestamp': { $gt: ?1 }}")
    List<Commit> findByCollectorItemIdAndScmCommitTimestamp(ObjectId collectorItemid, Long scmCommitTimestampThreshold);

}
