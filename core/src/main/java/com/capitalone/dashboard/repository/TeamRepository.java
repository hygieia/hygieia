package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.Team;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for {@link Team}.
 */
public interface TeamRepository extends CrudRepository<Team, ObjectId>,
        QueryDslPredicateExecutor<Team> {

    Team findByTeamId(String teamId);

    Team findByName(String name);

    /**
     * This essentially returns the max change date from the collection, based
     * on the last change date (or default delta change date property) available
     *
     * @param collectorId
     *            Collector ID of source system collector
     * @param changeDate
     *            Last available change date or delta begin date property
     * @return A single Change Date value that is the maximum value of the
     *         existing collection
     */
    @Query(value = "{ 'collectorId' : ?0, 'changeDate' : {$gt: ?1}, '_class' : 'com.capitalone.dashboard.model.Team', 'assetState': 'Active'}")
    List<Team> findTopByChangeDateDesc(ObjectId collectorId, String changeDate);

    @Query(value = "{ 'collectorId' : ?0 }")
    List<Team> findByCollectorId(ObjectId collectorId);

    Page<Team> findAllByCollectorIdAndNameContainingIgnoreCase(ObjectId collectorId, String name, Pageable pageable);
}
