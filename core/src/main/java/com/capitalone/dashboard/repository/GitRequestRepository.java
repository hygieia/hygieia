package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.GitRequest;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for {@link GitRequest} data.
 */
public interface GitRequestRepository  extends CrudRepository<GitRequest, ObjectId>, QueryDslPredicateExecutor<GitRequest> {


    List<GitRequest> findByCollectorItemIdAndRequestType(ObjectId collectorItemId, String requestType);

    @Query(value="{ 'collectorItemId' : ?0, 'requestType' : ?1}", fields="{ 'number' : 2, 'updatedAt' : 3 }")
    List<GitRequest> findRequestNumberAndLastUpdated(ObjectId collectorItemId, String requestType);

    GitRequest findByCollectorItemIdAndScmRevisionNumber(ObjectId collectorItemId, String revisionNumber);

    GitRequest findByCollectorItemIdAndNumberAndRequestType(ObjectId collectorItemId, String number, String requestType);

    @Query(value="{ 'collectorItemId': ?0, 'scmCommitTimestamp': { $gt: ?1 }}")
    List<GitRequest> findByCollectorItemIdAndScmCommitTimestamp(ObjectId collectorItemid,
                                                          Long scmCommitTimestampThreshold);

    GitRequest findByCollectorItemIdAndNumber(ObjectId collectorItemId, String number);
    //GitRequest findByOrgNameAndRepoNameAndNumberAndType(String orgName, String repoName, String number, String type);

    List<GitRequest> findByScmUrlAndScmBranchAndCreatedAtGreaterThanEqualAndMergedAtLessThanEqual(String scmUrl, String scmBranch, long beginDt, long endDt);

    List<GitRequest> findByScmUrlAndScmBranch(String scmUrl, String scmBranch, long beginDt, long endDt);

}
