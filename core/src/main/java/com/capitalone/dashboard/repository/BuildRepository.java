package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Build;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for {@link Build} data.
 */
public interface BuildRepository extends CrudRepository<Build, ObjectId>, QueryDslPredicateExecutor<Build> {

    /**
     * Finds the {@link Build} with the given number for a specific {@link com.capitalone.dashboard.model.CollectorItem}.
     *
     * @param collectorItemId collector item id
     * @param number buld number
     * @return a {@link Build}
     */
    Build findByCollectorItemIdAndNumber(ObjectId collectorItemId, String number);

    @Query(value="{'sourceChangeSet.scmRevisionNumber' : {$exists: true, $in: ?0}, 'collectorItemId': { $in: ?1 }}")
    List<Build> findBuildsForRevisionNumbersAndBuildCollectorItemIds(List<String> scmRevisionNumbers, List<ObjectId> buildCollectorItemId);

    Build findTop1ByCollectorItemIdOrderByTimestampDesc(ObjectId collectorItemId);
}
