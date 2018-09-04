package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.CodeQuality;
import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityMetricStatus;
import com.capitalone.dashboard.model.CodeQualityType;
import com.capitalone.dashboard.model.SonarProject;
import com.capitalone.dashboard.util.SonarDashboardUrl;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component("DefaultSonarClient")
public class DefaultSonarClient implements SonarClient {
    private static final Log LOG = LogFactory.getLog(DefaultSonarClient.class);

    protected static final String URL_RESOURCES = "/api/resources?format=json";
    protected static final String URL_RESOURCE_DETAILS = "/api/resources?format=json&resource=%s&metrics=%s&includealerts=true";
    protected static final String URL_QUALITY_PROFILES = "/api/qualityprofiles/search";
    protected static final String URL_QUALITY_PROFILE_PROJECT_DETAILS = "/api/qualityprofiles/projects?key=";
    protected static final String URL_QUALITY_PROFILE_CHANGES = "/api/qualityprofiles/changelog?profileKey=";

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
    public DefaultSonarClient(Supplier<RestOperations> restOperationsSupplier, SonarSettings settings) {
        this.httpHeaders = new HttpEntity<>(
                this.createHeaders(settings.getUsername(), settings.getPassword())
            );
        this.rest = restOperationsSupplier.get();
    }

    @Override
    public List<SonarProject> getProjects(String instanceUrl) {
        List<SonarProject> projects = new ArrayList<>();
        String url = instanceUrl + URL_RESOURCES;

        try {

            for (Object obj : parseAsArray(url)) {
                JSONObject prjData = (JSONObject) obj;

                SonarProject project = new SonarProject();
                project.setInstanceUrl(instanceUrl);
                project.setProjectId(str(prjData, ID));
                project.setProjectName(str(prjData, NAME));
                projects.add(project);
            }

        } catch (ParseException e) {
            LOG.error("Could not parse response from: " + url, e);
        } catch (RestClientException rce) {
            LOG.error(rce);
        }

        return projects;
    }


    @Override
    public CodeQuality currentCodeQuality(SonarProject project, String metrics) {
        String url = String.format(
                project.getInstanceUrl() + URL_RESOURCE_DETAILS, project.getProjectId(), metrics);

        try {
            JSONArray jsonArray = parseAsArray(url);

            if (!jsonArray.isEmpty()) {
                JSONObject prjData = (JSONObject) jsonArray.get(0);

                CodeQuality codeQuality = new CodeQuality();
                codeQuality.setName(str(prjData, NAME));
                codeQuality.setUrl(new SonarDashboardUrl(project.getInstanceUrl(), project.getProjectId()).toString());
                codeQuality.setType(CodeQualityType.StaticAnalysis);
                codeQuality.setTimestamp(timestamp(prjData, DATE));
                codeQuality.setVersion(str(prjData, VERSION));

                for (Object metricObj : (JSONArray) prjData.get(MSR)) {
                    JSONObject metricJson = (JSONObject) metricObj;

                    CodeQualityMetric metric = new CodeQualityMetric(str(metricJson, KEY));
                    metric.setValue(str(metricJson, VALUE));
                    metric.setFormattedValue(str(metricJson, FORMATTED_VALUE));
                    metric.setStatus(metricStatus(str(metricJson, ALERT)));
                    metric.setStatusMessage(str(metricJson, ALERT_TEXT));
                    codeQuality.getMetrics().add(metric);
                }

                return codeQuality;
            }

        } catch (ParseException e) {
            LOG.error("Could not parse response from: " + url, e);
        } catch (RestClientException rce) {
            LOG.error(rce);
        }

        return null;
    }
    
    public JSONArray getQualityProfiles(String instanceUrl) throws ParseException {
    	String url = instanceUrl + URL_QUALITY_PROFILES;
    	try {
    		JSONArray qualityProfileData = parseAsArray(url,"profiles");
    		return qualityProfileData;
    	} catch (ParseException e) {
    		LOG.error("Could not parse response from: " + url, e);
    		throw e;
    	} catch (RestClientException rce) {
    		LOG.error(rce);
    		throw rce;
    	}
    }
    
    public List<String> retrieveProfileAndProjectAssociation(String instanceUrl,String qualityProfile) throws ParseException{
    	List<String> projects = new ArrayList<>();
    	String url = instanceUrl + URL_QUALITY_PROFILE_PROJECT_DETAILS + qualityProfile;
    	try {
    		JSONArray associatedProjects = this.parseAsArray(url, "results");
    		if (!CollectionUtils.isEmpty(associatedProjects)) {
    			for (Object project : associatedProjects) {
    				JSONObject projectJson = (JSONObject) project;
    				String projectName = (String) projectJson.get("name");
    				projects.add(projectName);
    			}
    			return projects;
    		}
    		return null;
    	} catch (ParseException e) {
    		LOG.error("Could not parse response from: " + url, e);
    		throw e;
    	} catch (RestClientException rce) {
    		LOG.error(rce);
    		throw rce;
    	}
    }
    
   public JSONArray getQualityProfileConfigurationChanges(String instanceUrl,String qualityProfile) throws ParseException{
	   String url = instanceUrl + URL_QUALITY_PROFILE_CHANGES + qualityProfile;
	   try {
		   JSONArray qualityProfileConfigChanges = this.parseAsArray(url, "events");
		   return qualityProfileConfigChanges;
	   } catch (ParseException e) {
		   LOG.error("Could not parse response from: " + url, e);
		   throw e;
	   } catch (RestClientException rce) {
		   LOG.error(rce);
		   throw rce;
	   }
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

    protected CodeQualityMetricStatus metricStatus(String status) {
        if (StringUtils.isBlank(status)) {
            return CodeQualityMetricStatus.Ok;
        }

        switch(status) {
            case STATUS_WARN:  return CodeQualityMetricStatus.Warning;
            case STATUS_ALERT: return CodeQualityMetricStatus.Alert;
            default:           return CodeQualityMetricStatus.Ok;
        }
    }

    private final HttpHeaders createHeaders(String username, String password){
        HttpHeaders headers = new HttpHeaders();
        if (username != null && !username.isEmpty() &&
            password != null && !password.isEmpty()) {
          String auth = username + ":" + password;
          byte[] encodedAuth = Base64.encodeBase64(
              auth.getBytes(Charset.forName("US-ASCII"))
          );
          String authHeader = "Basic " + new String(encodedAuth);
          headers.set("Authorization", authHeader);
        }
        return headers;
    }
}
