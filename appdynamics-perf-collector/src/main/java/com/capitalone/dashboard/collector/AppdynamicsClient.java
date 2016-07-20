package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.AppdynamicsApplication;
import com.capitalone.dashboard.model.Performance;
import org.appdynamics.appdrestapi.RESTAccess;

import java.util.List;

public interface AppdynamicsClient {

    //List<AppdynamicsApplication> getApplications(String server);
    List<AppdynamicsApplication> getApplications(RESTAccess access);

    Performance getPerformanceMetrics(AppdynamicsApplication application, RESTAccess access);

}