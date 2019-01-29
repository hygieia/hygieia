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

@Component
public class DefaultSonar6Client implements SonarClient {
    private static final Log LOG = LogFactory.getLog(DefaultSonar6Client.class);

    private static final String URL_RESOURCES = "/api/components/search?qualifiers=TRK&ps=500";
    private static final String URL_RESOURCE_DETAILS = "/api/measures/component?format=json&componentId=%s&metricKeys=%s&includealerts=true";
    private static final String URL_PROJECT_ANALYSES = "/api/project_analyses/search?project=%s";
    private static final String URL_QUALITY_PROFILES = "/api/qualityprofiles/search";
    private static final String URL_QUALITY_PROFILE_PROJECT_DETAILS = "/api/qualityprofiles/projects?key=";
    private static final String URL_QUALITY_PROFILE_CHANGES = "/api/qualityprofiles/changelog?profileKey=";



    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String KEY = "key";
    private static final String METRIC = "metric";
    private static final String MSR = "measures";
    private static final String VALUE = "value";
    private static final String STATUS_WARN = "WARN";
    private static final String STATUS_ALERT = "ALERT";
    private static final String DATE = "date";
    private static final String EVENTS = "events";

    private final RestOperations rest;
    private final HttpEntity<String> httpHeaders;

    private static final String MINUTES_FORMAT = "%smin";
    private static final String HOURS_FORMAT = "%sh";
    private static final String DAYS_FORMAT = "%sd";
    private static final int HOURS_IN_DAY = 8;
    private static final int PAGE_SIZE=500;

    @Autowired
    public DefaultSonar6Client(Supplier<RestOperations> restOperationsSupplier, SonarSettings settings) {
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
            String key = "components";
            JSONArray jsonArray = getProjects(url, key);
            for (Object obj : jsonArray) {
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

    private JSONArray getProjects(String url, String key) throws ParseException {
        Long totalRecords = getTotalCount(parseJsonObject(url, "paging"));
        int pages = (int) Math.ceil((double)totalRecords / PAGE_SIZE);
        JSONArray jsonArray = new JSONArray();
        jsonArray = totalRecords > PAGE_SIZE ? getProjects(url, key, pages, jsonArray): getProjects(url, key, jsonArray);
        return jsonArray;
    }

    private JSONArray getProjects(String url, String key, JSONArray jsonArray) throws ParseException {
        jsonArray.addAll(parseAsArray(url, key));
        return jsonArray;
    }

    private JSONArray getProjects(String url, String key, int pages, JSONArray jsonArray) throws ParseException {
        int maxPages = 20;
        if(pages <= maxPages) {
            maxPages = pages;
        }
       for (int start=1;start<=maxPages;start++){
            getProjects(url, key, jsonArray, start);
        }
        return  jsonArray;
    }

    private void getProjects(String url, String key, JSONArray jsonArray, int pageNumber) throws ParseException {
        String urlFinal = url+"&p="+pageNumber;
        jsonArray.addAll(parseAsArray(urlFinal, key));
    }

    @Override
    public CodeQuality currentCodeQuality(SonarProject project, String metrics) {
        String url = String.format(
                project.getInstanceUrl() + URL_RESOURCE_DETAILS, project.getProjectId(), metrics);

        try {
            ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET, this.httpHeaders, String.class);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
            String key = "component";

            if (jsonObject != null) {
                JSONObject prjData = (JSONObject) jsonObject.get(key);

                CodeQuality codeQuality = new CodeQuality();
                codeQuality.setType(CodeQualityType.StaticAnalysis);
                codeQuality.setName(str(prjData, NAME));
                codeQuality.setUrl(new SonarDashboardUrl(project.getInstanceUrl(), str(prjData, KEY)).toString());

                url = String.format(
                        project.getInstanceUrl() + URL_PROJECT_ANALYSES, str(prjData, KEY));
                key = "analyses";
                JSONArray jsonArray = parseAsArray(url, key);
                getProjectAnalysis(codeQuality, jsonArray);
                for (Object metricObj : (JSONArray) prjData.get(MSR)) {
                    JSONObject metricJson = (JSONObject) metricObj;

                    CodeQualityMetric metric = new CodeQualityMetric(str(metricJson, METRIC));
                    metric.setValue(str(metricJson, VALUE));
                    if (metric.getName().equals("sqale_index")) {
                        metric.setFormattedValue(format(str(metricJson, VALUE)));
                    } else if (strSafe(metricJson, VALUE).indexOf(".") > 0) {
                        metric.setFormattedValue(str(metricJson, VALUE) + "%" );
                    } else if (strSafe(metricJson, VALUE).matches("\\d+")) {
                        metric.setFormattedValue(String.format("%,d", integer(metricJson, VALUE)));
                    } else {
                        metric.setFormattedValue(str(metricJson, VALUE));
                    }
                    codeQuality.getMetrics().add(metric);
                }

                return codeQuality;
            }

        } catch (ParseException e) {
            LOG.error("Could not parse response from: " + url, e);
        } catch (RestClientException rce) {
            LOG.error("Rest Client Exception: " + url + ":" + rce.getMessage());
        }

        return null;
    }

    private void getProjectAnalysis(CodeQuality codeQuality, JSONArray jsonArray) {
        if(jsonArray!=null && !jsonArray.isEmpty()) {
            JSONObject prjLatestData = (JSONObject) jsonArray.get(0);
            codeQuality.setTimestamp(timestamp(prjLatestData, DATE));
            for (Object eventObj : (JSONArray) prjLatestData.get(EVENTS)) {
                JSONObject eventJson = (JSONObject) eventObj;

                if (strSafe(eventJson, "category").equals("VERSION")) {
                    codeQuality.setVersion(str(eventJson, NAME));
                }
            }
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
    
    public JSONArray getQualityProfiles(String instanceUrl) throws ParseException {
    	String url = instanceUrl + URL_QUALITY_PROFILES;
    	try {
    		JSONArray qualityProfileData = this.parseAsArray(url,"profiles");
    		return qualityProfileData;
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

    private JSONArray parseAsArray(String url, String key) throws ParseException {
        JSONObject jsonObject = getResponse(url);
        return (JSONArray) jsonObject.get(key);
    }

    private JSONObject parseJsonObject(String url, String key) throws ParseException {
        JSONObject jsonObject = getResponse(url);
        return (JSONObject)jsonObject.get(key);
    }

    private JSONObject getResponse(String url) throws ParseException {
        ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET, this.httpHeaders, String.class);
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
        LOG.debug(url);
        return jsonObject;
    }

    private long timestamp(JSONObject json, String key) {
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

    private String str(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? null : obj.toString();
    }

    private String strSafe(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? "" : obj.toString();
    }

    @SuppressWarnings("unused")
    private Integer integer(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? null : Integer.valueOf(obj.toString());
    }

    @SuppressWarnings("unused")
    private BigDecimal decimal(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? null : new BigDecimal(obj.toString());
    }

    @SuppressWarnings("unused")
    private Boolean bool(JSONObject json, String key) {
        Object obj = json.get(key);
        return obj == null ? null : Boolean.valueOf(obj.toString());
    }

    @SuppressWarnings("unused")
    private String format(String duration) {
        Long durationInMinutes = Long.valueOf(duration);
        if (durationInMinutes == 0) {
            return "0";
        }
        boolean isNegative = durationInMinutes < 0;
        Long absDuration = Math.abs(durationInMinutes);

        int days = ((Double) ((double) absDuration / HOURS_IN_DAY / 60)).intValue();
        Long remainingDuration = absDuration - (days * HOURS_IN_DAY * 60);
        int hours = ((Double) (remainingDuration.doubleValue() / 60)).intValue();
        remainingDuration = remainingDuration - (hours * 60);
        int minutes = remainingDuration.intValue();

        return format(days, hours, minutes, isNegative);
    }

    @SuppressWarnings("PMD")
    private static String format(int days, int hours, int minutes, boolean isNegative) {
        StringBuilder message = new StringBuilder();
        if (days > 0) {
            message.append(String.format(DAYS_FORMAT, isNegative ? (-1 * days) : days));
        }
        if (displayHours(days, hours)) {
            addSpaceIfNeeded(message);
            message.append(String.format(HOURS_FORMAT, isNegative && message.length() == 0 ? (-1 * hours) : hours));
        }
        if (displayMinutes(days, hours, minutes)) {
            addSpaceIfNeeded(message);
            message.append(String.format(MINUTES_FORMAT, isNegative && message.length() == 0 ? (-1 * minutes) : minutes));
        }
        return message.toString();
    }

    private static void addSpaceIfNeeded(StringBuilder message) {
        if (message.length() > 0) {
            message.append(" ");
        }
    }

    private static boolean displayHours(int days, int hours) {
        return hours > 0 && days < 10;
    }

    private static boolean displayMinutes(int days, int hours, int minutes) {
        return minutes > 0 && hours < 10 && days == 0;
    }

    private CodeQualityMetricStatus metricStatus(String status) {
        if (StringUtils.isBlank(status)) {
            return CodeQualityMetricStatus.Ok;
        }

        switch(status) {
            case STATUS_WARN:  return CodeQualityMetricStatus.Warning;
            case STATUS_ALERT: return CodeQualityMetricStatus.Alert;
            default:           return CodeQualityMetricStatus.Ok;
        }
    }

    private HttpHeaders createHeaders(String username, String password){
        HttpHeaders headers = new HttpHeaders();
        if (username != null && !username.isEmpty()) {
            String auth = username + ":" + (password == null ? "" : password);
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII"))
            );
            String authHeader = "Basic " + new String(encodedAuth);
            headers.set("Authorization", authHeader);
        }
        return headers;
    }

    private Long getTotalCount(JSONObject pagingObject) {
        return (Long) pagingObject.get("total");
    }

}
