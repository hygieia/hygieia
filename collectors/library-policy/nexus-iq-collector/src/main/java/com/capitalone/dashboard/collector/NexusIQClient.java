package com.capitalone.dashboard.collector;

import java.util.List;

import com.capitalone.dashboard.model.LibraryPolicyReport;
import com.capitalone.dashboard.model.LibraryPolicyResult;
import com.capitalone.dashboard.model.NexusIQApplication;
import com.capitalone.dashboard.model.PolicyScanMetric;

public interface NexusIQClient {

    List<NexusIQApplication> getApplications(String instanceUrl);
    List<LibraryPolicyReport> getApplicationReport(NexusIQApplication application);    
    LibraryPolicyResult getDetailedReport (String url);
    PolicyScanMetric getPolicyAlerts(NexusIQApplication application);
}
