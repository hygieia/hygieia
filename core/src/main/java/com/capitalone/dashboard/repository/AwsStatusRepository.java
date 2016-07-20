package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.AwsStatus;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AwsStatusRepository extends PagingAndSortingRepository<AwsStatus, ObjectId> {

    /**
     * Find all {@link AwsStatus}s for a given {@link com.capitalone.dashboard.model.Dashboard}.
     *
     * @param dashboardId dashboard id
     * @return list of {@link AwsStatus}s
     */
    List<AwsStatus> findByDashboardId(ObjectId dashboardId);
}
