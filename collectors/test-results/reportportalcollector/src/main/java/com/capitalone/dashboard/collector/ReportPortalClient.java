package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.ReportPortalProject;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

public interface ReportPortalClient {

    List<ReportPortalProject> getProjects(String instanceUrl);
    CodeQuality currentCodeQuality(ReportPortalProject project, String metrics);
    JSONArray getQualityProfiles(String instanceUrl) throws ParseException;
    List<String> retrieveProfileAndProjectAssociation(String instanceUrl,String qualityProfile) throws ParseException;
    JSONArray getQualityProfileConfigurationChanges(String instanceUrl,String qualityProfile) throws ParseException; 

}
