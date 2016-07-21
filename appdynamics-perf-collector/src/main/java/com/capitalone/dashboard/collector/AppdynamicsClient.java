package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.AppdynamicsApplication;
import com.capitalone.dashboard.model.Performance;
import org.appdynamics.appdrestapi.RESTAccess;

import java.util.Set;

public interface AppdynamicsClient {

    //List<AppdynamicsApplication> getApplications(String server);
    Set<AppdynamicsApplication> getApplications(RESTAccess restClient);

    Performance getPerformanceMetrics(AppdynamicsApplication application, RESTAccess restClient);

}