package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.AppdynamicsApplication;
import com.capitalone.dashboard.model.Performance;

import java.util.List;

public interface AppdynamicsClient {

    List<AppdynamicsApplication> getApplications(String server);
    Performance getPerformance(AppdynamicsApplication application);

}