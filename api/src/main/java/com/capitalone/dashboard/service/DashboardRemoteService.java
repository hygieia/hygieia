package com.capitalone.dashboard.service;

import com.capitalone.dashboard.misc.HygieiaException;
import com.capitalone.dashboard.model.Dashboard;
import com.capitalone.dashboard.request.DashboardRemoteRequest;

public interface DashboardRemoteService {
    Dashboard remoteCreate(DashboardRemoteRequest request) throws HygieiaException;
}




