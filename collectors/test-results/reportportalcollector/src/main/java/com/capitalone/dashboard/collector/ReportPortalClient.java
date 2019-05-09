package com.capitalone.dashboard.collector;



import java.util.List;

import org.bson.types.ObjectId;

import com.capitalone.dashboard.model.ReportPortalCollector;
import com.capitalone.dashboard.model.ReportPortalProject;
import com.capitalone.dashboard.model.ReportResult;



public interface ReportPortalClient {

    List<ReportPortalProject> getProjectData(String instanceUrl ,String projectName);

	

	List<ReportResult> getTestData(ReportPortalCollector collector, ReportPortalProject project);



    

}
