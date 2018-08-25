package com.capitalone.dashboard.collector;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import com.capitalone.dashboard.model.RallyBurnDownData;
import com.capitalone.dashboard.model.RallyFeature;
import com.capitalone.dashboard.model.RallyProject;
import com.capitalone.dashboard.model.RallyStoryStages;

public interface RallyClient {

    List<RallyProject> getProjects(String instanceUrl) throws ParseException;
    
    List<RallyFeature> getRallyIterations(RallyProject project);
    
	RallyBurnDownData getBurnDownData(RallyFeature iteration, JSONArray iterationArray, RallyBurnDownData rallyBurnDownData);

	List<String> getIterationPeriodRange(String startDate, String endDate);
    
	JSONArray getIterationStories(RallyFeature iteration) throws ParseException;
	
	RallyStoryStages getStoryStages(String rallyProjectId, JSONArray iterationArray);
}
