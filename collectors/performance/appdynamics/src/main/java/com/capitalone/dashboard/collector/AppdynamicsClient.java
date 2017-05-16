package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.AppdynamicsApplication;
import com.capitalone.dashboard.model.PerformanceMetric;

import java.util.List;
import java.util.Set;

public interface AppdynamicsClient {

    //List<AppdynamicsApplication> getApplications(String server);
    Set<AppdynamicsApplication> getApplications(String instanceURL);

    List<PerformanceMetric> getPerformanceMetrics(AppdynamicsApplication application, String instanceUrl);
}