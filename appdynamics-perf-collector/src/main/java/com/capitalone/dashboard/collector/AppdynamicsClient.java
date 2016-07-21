package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.AppdynamicsApplication;
import com.capitalone.dashboard.model.Performance;

import java.util.Set;

public interface AppdynamicsClient {

    //List<AppdynamicsApplication> getApplications(String server);
    Set<AppdynamicsApplication> getApplications();

    Performance getPerformanceMetrics(AppdynamicsApplication application);

}