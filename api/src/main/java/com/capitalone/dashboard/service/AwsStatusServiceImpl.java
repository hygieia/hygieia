package com.capitalone.dashboard.service;

import com.capitalone.dashboard.model.AwsStatus;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.repository.AwsStatusRepository;
import com.capitalone.dashboard.repository.DashboardRepository;
import com.capitalone.dashboard.request.AwsStatusDataCreateRequest;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Hits the repositories for saving the data to mongo and retrieving it.
@Service
public class AwsStatusServiceImpl implements AwsStatusService {
    private final AwsStatusRepository awsStatusRepository;
    private final DashboardRepository dashboardRepository;

    @Autowired
    public AwsStatusServiceImpl(AwsStatusRepository awsStatusRepository, DashboardRepository dashboardRepository) {
        this.awsStatusRepository = awsStatusRepository;
        this.dashboardRepository = dashboardRepository;
    }

    @Override
    public Iterable<AwsStatus> all() {
        return awsStatusRepository.findAll();
    }

    @Override
    public List<AwsStatus> dashboardAwsStatuses(ObjectId dashboardId) {
        return awsStatusRepository.findByDashboardId(dashboardId);
    }

    @Override
    public AwsStatus get(ObjectId awsStatusId) {
        return awsStatusRepository.findOne(awsStatusId);
    }

    @Override
    public AwsStatus create(ObjectId dashboardId, AwsStatusDataCreateRequest awsStatusDataCreateRequest) {
        AwsStatus awsStatus = new AwsStatus();
        awsStatus.setName(awsStatusDataCreateRequest.getName());
        awsStatus.setUrl(awsStatusDataCreateRequest.getUrl());
        awsStatus.setDashboardId(dashboardId);
        awsStatus.setLastUpdated(System.currentTimeMillis());

        Dashboard dashboard = dashboardRepository.findOne(dashboardId);
        awsStatus.setApplicationName(dashboard.getApplication().getName());

        return awsStatusRepository.save(awsStatus);
    }

    @Override
    public AwsStatus update(ObjectId dashboardId, AwsStatus awsStatus) {
        if (!awsStatus.getDashboardId().equals(dashboardId)) {
            throw new IllegalStateException("Not allowed to update this service from this dashboard!");
        }
        awsStatus.setLastUpdated(System.currentTimeMillis());
        return awsStatusRepository.save(awsStatus);
    }

    @Override
    public void delete(ObjectId dashboardId, ObjectId awsStatusId) {
        AwsStatus service = get(awsStatusId);
        if (!service.getDashboardId().equals(dashboardId)) {
            throw new IllegalStateException("Not allowed to delete this service from this dashboard!");
        }
        awsStatusRepository.delete(service);
    }
}
