package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.SonarProject;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

public interface SonarClient {

    List<SonarProject> getProjects(String instanceUrl);
    CodeQuality currentCodeQuality(SonarProject project, String metrics);
    JSONArray getQualityProfiles(String instanceUrl) throws ParseException;
    List<String> retrieveProfileAndProjectAssociation(String instanceUrl,String qualityProfile) throws ParseException;
    JSONArray getQualityProfileConfigurationChanges(String instanceUrl,String qualityProfile) throws ParseException; 

}
