package com.capitalone.dashboard.repository;

import java.util.List;

import com.capitalone.dashboard.model.pullrequest.PullRequest;
import org.bson.types.ObjectId;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface PullRequestRepository extends CrudRepository<PullRequest, ObjectId>, QueryDslPredicateExecutor<PullRequest> {

    PullRequest findByCollectorItemIdAndIdAndUpdatedDate(ObjectId collectorItemId, long id, Long updatedDate);

    PullRequest findByCollectorItemIdAndId(ObjectId collectorItemId, long id);

    PullRequest findById(long id);

    @SuppressWarnings("all")
        //without this have a pmd violation for using underscore in function naming
    List<PullRequest> findByCollectorItemIdAndFromRef_Repository_NameAndOpenOrderByCreatedDate(ObjectId collectorItemId, String repoName, boolean open);

    @SuppressWarnings("all")
        //without this have a pmd violation for using underscore in function naming 
    List<PullRequest> findByFromRef_Repository_NameAndOpenOrderByCreatedDateAsc(String repoName, boolean open);
}
