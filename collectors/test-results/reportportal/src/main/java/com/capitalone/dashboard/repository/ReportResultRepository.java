package com.capitalone.dashboard.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


import com.capitalone.dashboard.model.ReportResult;
/**
 * Repository for {@link ReportResult} data.
 */

public interface ReportResultRepository extends TestResultRepository {

    /**
     * Finds the {@link ReportResult} with the given launch ID for a specific
     * {@link com.capitalone.dashboard.model.CollectorItem}.
     *
     * @param collectorId collector item ID
     * @param launchId launch ID
     * @return a ReportResult
     */
    //ReportResult findByIdAndlaunchId(ObjectId Id, String launchId);
 
    @Query(value = "{ 'testId' : ?0 }")
    ReportResult findBytestId(String testId);

    ReportResult findByCollectorId(ObjectId collectorItemId);
    

}