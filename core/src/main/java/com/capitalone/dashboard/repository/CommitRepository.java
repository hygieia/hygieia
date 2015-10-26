package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Commit;
import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

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
}
