package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.AppdynamicsApplication;

import java.util.Map;
import java.util.Set;

public interface AppdynamicsClient {

    Set<AppdynamicsApplication> getApplications(String instanceURL);

    Map<String,Object> getPerformanceMetrics(AppdynamicsApplication application, String instanceUrl);
}