package com.capitalone.dashboard.collector;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.ReportPortalCollector;
import com.capitalone.dashboard.model.ReportPortalProject;
import com.capitalone.dashboard.model.ReportResult;
import com.capitalone.dashboard.util.Supplier;

@Component("DefaultReportPortalClient")
public class DefaultReportPortalClient implements ReportPortalClient {
    private static final Log LOG = LogFactory.getLog(DefaultReportPortalClient.class);

    protected static final String URL_RESOURCES = "/api/v1/";
    
    protected static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    protected static final String ID = "id";
    protected static final String NAME = "name";
    protected static final String KEY = "key";
    protected static final String VERSION = "version";
    protected static final String MSR = "msr";
    protected static final String ALERT = "alert";
    protected static final String ALERT_TEXT = "alert_text";
    protected static final String VALUE = "val";
    protected static final String FORMATTED_VALUE = "frmt_val";
    protected static final String STATUS_WARN = "WARN";
    protected static final String STATUS_ALERT = "ALERT";
    protected static final String DATE = "date";

    protected final RestOperations rest;
    protected final HttpEntity<String> httpHeaders;

    @Autowired
    public DefaultReportPortalClient(Supplier<RestOperations> restOperationsSupplier, ReportPortalSettings settings) {
        this.httpHeaders = new HttpEntity<String>(
                this.createHeaders(settings.getBearerToken())
            );
        this.rest = restOperationsSupplier.get();
    }

    @Override
    public List<ReportPortalProject> getProjectData(String instanceUrl,String projectName) {
        List<ReportPortalProject> projects = new ArrayList<>();
        String url = instanceUrl + URL_RESOURCES + projectName +"/launch/latest?page.sort=name&page.size=100";
        
        try {

            for (Object obj : parseAsArray(url,"content")) {
                JSONObject prjData = (JSONObject) obj;

                ReportPortalProject project = new ReportPortalProject();
                
                //project.setInstanceUrl(instanceUrl);
                
               // project.setLastUpdated();
                
				
            
                Map<String, Object> Options = new HashMap<String, Object>(prjData);
                project.setOptions(Options);
                project.setProjectId(str(prjData, ID));
                project.setProjectName(str(prjData, NAME));
                project.setLaunchNumber(str(prjData,"number"));
                project.setInstanceUrl(url);
                project.setDescription(str(prjData,"description"));
                projects.add(project);
                
				//Map<String, Object> Options = new HashMap<String, Object>(prjData);
               // project.setOptions(Options);
            }

        } catch (ParseException e) {
            LOG.error("Could not parse response from: " + url, e);
        } catch (RestClientException rce) {
            LOG.error(rce);
        }

        return projects;
    }
    
    
  


	protected JSONArray parseAsArray(String url) throws ParseException {
        ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET, this.httpHeaders, String.class);
        return (JSONArray) new JSONParser().parse(response.getBody());
    }

    protected JSONArray parseAsArray(String url, String key) throws ParseException {
        ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET, this.httpHeaders, String.class);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
        LOG.debug(url);
        return (JSONArray) jsonObject.get(key);
    }

    protected long timestamp(JSONObject json, String key) {
        Object obj = json.get(key);
        if (obj != null) {
            try {
                return new SimpleDateFormat(DATE_FORMAT).parse(obj.toString()).getTime();
            } catch (java.text.ParseException e) {
                LOG.error(obj + " is not in expected format " + DATE_FORMAT, e);
            }
        }
        return 0;
    }

    protected String str(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? null : obj.toString();
    }
    @SuppressWarnings("unused")
    protected Integer integer(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? null : (Integer) obj;
    }

    @SuppressWarnings("unused")
    protected BigDecimal decimal(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? null : new BigDecimal(obj.toString());
    }

    @SuppressWarnings("unused")
    protected Boolean bool(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? null : Boolean.valueOf(obj.toString());
    }

    private final HttpHeaders createHeaders(String bearerToken){
        HttpHeaders headers = new HttpHeaders();
      //  headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
     
        
        String authHeader = "bearer "+bearerToken;
        headers.set("Authorization", authHeader);
        return headers;
    }

	@Override
	public List<ReportResult> getTestData(ReportPortalCollector collector, String launchId,String instanceUrl) {
		// TODO Auto-generated method stub
		List<ReportResult> tests = new ArrayList<>();
		String projectName=collector.getProjectName();
		
        String url = instanceUrl + URL_RESOURCES + projectName +"/item?filter.eq.launch="+launchId;
        
        try {

            for (Object obj : parseAsArray(url,"content")) {
                JSONObject testData = (JSONObject) obj;
                
                ReportResult test = new ReportResult();
                Map<String, Object> Results = new HashMap<String, Object>(testData);
                test.setResults(Results);
                //project.setInstanceUrl(instanceUrl);
                
               // project.setLastUpdated();
                
				test.setCollectorId(collector.getId());
				test.setTestId(str(testData,"id"));
				test.setLaunchId(launchId);
				test.setName(str(testData,"name"));
				
				tests.add(test);
                
				//Map<String, Object> Options = new HashMap<String, Object>(prjData);
               // project.setOptions(Options);
            }

        } catch (ParseException e) {
            LOG.error("Could not parse response from: " + url, e);
        } catch (RestClientException rce) {
            LOG.error(rce);
        }

        return tests;
    
		
		
	}

	
}
