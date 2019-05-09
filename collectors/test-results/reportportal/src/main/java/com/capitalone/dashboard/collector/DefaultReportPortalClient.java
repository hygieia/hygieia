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
import org.bson.types.ObjectId;
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
import com.capitalone.dashboard.model.TestCapability;
import com.capitalone.dashboard.model.TestCase;
import com.capitalone.dashboard.model.TestCaseStatus;
import com.capitalone.dashboard.model.TestCaseStep;
import com.capitalone.dashboard.model.TestResult;
import com.capitalone.dashboard.model.TestSuite;
import com.capitalone.dashboard.model.TestSuiteType;
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
       // LOG.info(url);
        //LOG.info(jsonObject.get("error_code"));
   
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
    protected Long Long(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? null : (Long) obj;
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
	public List<ReportResult> getTestData(ReportPortalCollector collector, String launchId,String instanceUrl,ObjectId collectorItemId) {
		// TODO Auto-generated method stub
		List<ReportResult> reports = new ArrayList<>();
		ReportResult report = new ReportResult();
		List<TestCapability> caps=new ArrayList<>();
		List<TestSuite> suites=new ArrayList<>();
		//List<TestCase> cases=new ArrayList<>();
		TestCapability cap=new TestCapability();
		//List<TestCaseStep> steps=new ArrayList<>();
		//test.setTestCapabilities(caps);
		String projectName=collector.getProjectName();
		
        String url = instanceUrl + "/api/v1/" + projectName +"/item?filter.eq.launch="+ launchId+"&filter.eq.type="+"SUITE";
        //int count=0;
        //JSONObject testData =new JSONObject();
        try {

            for (Object obj : parseAsArray(url,"content")) {
            	JSONObject testData = (JSONObject) obj;
                 
               // LOG.info(testData.get(str(testData,"name")));
               TestSuite suite=getTestSuite(testData,instanceUrl,collector);
                
               suites.add(suite);
               
              // count++;
                
            }
            
            //LOG.info(count);
            
            cap.setDescription(projectName);
            cap.setExecutionId(launchId);
            cap.setToolType("reporting");
            cap.setTestSuites(suites);
            cap.setType(TestSuiteType.Functional);
            caps.add(cap);
            
            // Map<String, Object> Results = new HashMap<String, Object>(testData);
           //  report.setResults(Results);
             report.setDescription(projectName);
             report.setExecutionId(launchId);
             report.setUrl(instanceUrl);
             report.setCollectorId(collector.getId());
             report.setTestId(launchId);
             report.setExecutionId(launchId);
             report.setCollectorItemId(collectorItemId);
             report.setTestCapabilities(caps);
             reports.add(report);
                //project.setInstanceUrl(instanceUrl);
                
               // project.setLastUpdated();
               /* test.setDescription(str(testData,"name"));
				test.setCollectorId(collector.getId());
				test.setTestId(str(testData,"id"));
				test.setLaunchId(launchId);
				test.setName(str(testData,"name"));
				test.setExecutionId(launchId);
				
				tests.add(test);
				*/
                
				//Map<String, Object> Options = new HashMap<String, Object>(prjData);
               // project.setOptions(Options);
            

        } catch (ParseException e) {
            LOG.error("Could not parse response from: " + url, e);
        } catch (RestClientException rce) {
            LOG.error(rce);
        }

        return reports;
    
		
		
	}

	private List<TestCaseStep> getStepData(String parent, String launchId, String instanceUrl, String projectName) {
		// TODO Auto-generated method stub
		List<TestCaseStep> step=new ArrayList<>();
		 String step_url = instanceUrl + URL_RESOURCES + projectName +"/item?filter.eq.launch="+launchId+"&filter.eq.type=STEP&filter.eq.parent="+parent;
	        
			List<TestCaseStep> steps=new ArrayList<>();
     try {

         for (Object obj : parseAsArray(step_url,"content")) {
             JSONObject testData = (JSONObject) obj;
             TestCaseStep step1=new TestCaseStep();
     		step1.setDescription(str(testData,"name"));
     		step1.setStatus(getStatus(str(testData,"status")));
     		JSONObject stats=(JSONObject) testData.get("statistics");
     		JSONObject executions=(JSONObject) stats.get("executions");
     		//step.setFailedTestStepCount((integer(executions,"failed")));
     		//step.setSuccessTestStepCount(integer(executions,"failed"));
     		//step.setTotalTestStepCount(integer(executions,"total"));
     		//step.setSkippedTestStepCount(integer(executions,"skipped"));
     		List<TestCaseStep> teststeps=getStepData(str(testData,"id"),str(testData,"launchId"),instanceUrl,projectName);
     		//step.setTestSteps(teststeps);
             steps.add(step1);
             
             
         }
     }catch (ParseException e) {
         LOG.error("Could not parse response from: " + step_url, e);
     } catch (RestClientException rce) {
         LOG.error(rce);
     }
         
         return steps;
	}

	private List<TestCase> getTestCases(String parent,String launchId,String instanceUrl,String projectName) {
		// TODO Auto-generated method stub
 String test_url = instanceUrl + URL_RESOURCES + projectName +"/item?filter.eq.launch="+launchId+"&filter.eq.type=TEST&filter.eq.parent="+parent;
	        
 				List<TestCase> tests=new ArrayList<>();
	        try {

	            for (Object obj : parseAsArray(test_url,"content")) {
	                JSONObject testData = (JSONObject) obj;
	                TestCase test=new TestCase();
	        		test.setDescription(str(testData,"name"));
	        		test.setStatus(getStatus(str(testData,"status")));
	        		JSONObject stats=(JSONObject) testData.get("statistics");
	        		JSONObject executions=(JSONObject) stats.get("executions");
	        		test.setFailedTestStepCount(Integer.parseInt(str(executions,"failed")));
	        		test.setSuccessTestStepCount(Integer.parseInt(str(executions,"failed")));
	        		test.setTotalTestStepCount(Integer.parseInt(str(executions,"total")));
	        		test.setSkippedTestStepCount(Integer.parseInt(str(executions,"skipped")));
	        		List<TestCaseStep> teststeps=getStepData(str(testData,"id"),str(testData,"launchId"),instanceUrl,projectName);
	        		test.setTestSteps(teststeps);
	                tests.add(test);
	                
	                
	            }
	        }catch (ParseException e) {
	            LOG.error("Could not parse response from: " + test_url, e);
	        } catch (RestClientException rce) {
	            LOG.error(rce);
	        }
	            
	            return tests;
		
	}

	
	

	private TestSuite getTestSuite(JSONObject testData,String instanceUrl,ReportPortalCollector collector) {
		// TODO Auto-generated method stub
		TestSuite suite=new TestSuite();
		suite.setDescription(str(testData,"name"));
		suite.setStatus(getStatus(str(testData,"status")));
		
		suite.setStartTime(Long(testData,"start_time"));
		suite.setEndTime(Long(testData,"end_time"));
		JSONObject stats=(JSONObject) testData.get("statistics");
		JSONObject executions=(JSONObject) stats.get("executions");
		suite.setFailedTestCaseCount(Integer.parseInt(str(executions,"failed")));
		suite.setSuccessTestCaseCount(Integer.parseInt(str(executions,"failed")));
		suite.setTotalTestCaseCount(Integer.parseInt(str(executions,"total")));
		suite.setSkippedTestCaseCount(Integer.parseInt(str(executions,"skipped")));
		List<TestCase> testcases=getTestCases(str(testData,"id"),str(testData,"launchId"),instanceUrl,collector.getProjectName());
		suite.setTestCases(testcases);
		
		return suite;
	}

	private TestCaseStatus getStatus(String str) {
		// TODO Auto-generated method stub
		
		switch (str) {
        case "PASSES":
            return TestCaseStatus.Success;
            
        case "FAILED":
        	return TestCaseStatus.Failure;
           
        case "SKIPPED":
        	return TestCaseStatus.Skipped;
            
        default:
        	return TestCaseStatus.Unknown;
    }
		
	}

	
}
