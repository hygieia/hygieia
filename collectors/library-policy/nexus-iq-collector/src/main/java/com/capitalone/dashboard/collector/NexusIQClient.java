package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.LibraryPolicyReport;
import com.capitalone.dashboard.model.LibraryPolicyResult;
import com.capitalone.dashboard.model.NexusIQApplication;

import java.util.List;
import java.util.Map;

public interface NexusIQClient {

    List<NexusIQApplication> getApplications(String instanceUrl);
    List<LibraryPolicyReport> getApplicationReport(NexusIQApplication application);
    LibraryPolicyResult getDetailedReport (String url);
}
