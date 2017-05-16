package com.capitalone.dashboard.collector;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.capitalone.dashboard.model.WLMonitorCollector;
import com.capitalone.dashboard.model.WLMonitorCollectorItem;
import com.capitalone.dashboard.model.WebLogicMonitor;
import com.capitalone.dashboard.repository.WLMonitorApplicationRepository;
import com.capitalone.dashboard.repository.WLMonitorRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import com.capitalone.dashboard.util.Supplier;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("PMD")
@Component
public class WLMonitorInfoTask implements WLMonitorClient{
	private static final Logger LOG = LoggerFactory.getLogger(WLMonitorInfoTask.class);
	private static final String WLMONITOR_URL_SUFFIX = "/management/tenant-monitoring/servers";
	private final RestOperations rest;	
	private final WLMonitorSettings wLMonitorSettings;
	private final WLMonitorApplicationRepository wLMonitorApplicationRepository;
	private final WLMonitorRepository wLMonitorRepository;
	private final MongoTemplate mongoTemplate;
	@Autowired
	public WLMonitorInfoTask(Supplier<RestOperations> restOperationsSupplier,WLMonitorSettings wLMonitorSettings,
							 WLMonitorApplicationRepository wLMonitorApplicationRepository,WLMonitorRepository wLMonitorRepository,
								MongoTemplate mongoTemplate) {
		 this.rest = restOperationsSupplier.get();
		this.wLMonitorSettings = wLMonitorSettings;
		this.wLMonitorApplicationRepository = wLMonitorApplicationRepository;
		this.wLMonitorRepository =  wLMonitorRepository;
		this.mongoTemplate = mongoTemplate;
	}	  
	
	@Override
	public List<WebLogicMonitor> getWLMonitorEnvironments(String instanceUrl,String envName) {
		List<WebLogicMonitor> result = new ArrayList<WebLogicMonitor>();
		try {
			String url = joinURL(instanceUrl, WLMONITOR_URL_SUFFIX);
			System.out.println("instanceUrl:::"+instanceUrl);
			//String url = instanceUrl+VMONITOR_URL_SUFFIX;
			System.out.println("url:::"+url);
			ResponseEntity<String> responseEntity = makeRestCall(url);
			String returnJSON = responseEntity.getBody();
			JSONParser parser = new JSONParser();
			try {
				JSONObject object = (JSONObject) parser.parse(returnJSON);
				 Object body = object.get("body");
				 JSONObject servers = (JSONObject) body;
				 for (Object server : getJsonArray(servers, "items")) {
	                    JSONObject jsonServer = (JSONObject) server;
	                    String name = jsonServer.get("name") != null ? jsonServer.get("name").toString(): "";
	                    String state = jsonServer.get("state") != null ? jsonServer.get("state").toString() : "";
	                    String health = jsonServer.get("health") != null ? jsonServer.get("health").toString() : "";
	                    //if(isNewData(envName,name,state,health)){
	                    	System.out.println("New Record:::::::::::::::::::::");
	                    	WebLogicMonitor vm = new WebLogicMonitor();
	                    	vm.setEnvironment(envName);
	                    	vm.setName(name);
	                    	vm.setState(state);
	                    	vm.setHealth(health);
							if (state.equalsIgnoreCase("running"))
								vm.setStatus("UP");
							else vm.setStatus("DOWN");
							vm.setTimestamp(System.currentTimeMillis());
	                    	wLMonitorRepository.save(vm);
	                    	result.add(vm);
	                    //}
				 }
			
			} catch (ParseException e) {
				LOG.error("Parsing jobs on instance: " + url, e);
			}
		} catch (ResourceAccessException rae){
			LOG.error("ResourceAccessException received for " + instanceUrl);
			insertDownRecords(envName);
		}catch (SocketTimeoutException ste){
			LOG.error("SocketTimeoutException received for " + instanceUrl);
			insertDownRecords(envName);
		}
		catch (RestClientException rce) {
			LOG.error("client exception loading jobs", rce);
			insertDownRecords(envName);
		} catch (MalformedURLException mfe) {
			LOG.error("malformed url for loading jobs", mfe);
		}
		return result;
	}

	private List<WebLogicMonitor> insertDownRecords(String envName){
		List<WebLogicMonitor> result = new ArrayList<WebLogicMonitor>();
		Criteria criteria = new Criteria();
		Criteria criteria1 = criteria.where("environment").is(envName);
		Query query = new Query(criteria1);
		//BasicDBObject dbObject = new BasicDBObject("environment",envName);
		List<String> servers = mongoTemplate.getCollection("weblogic_monitor")
				.distinct("name",new BasicDBObject("environment",envName));
		for (String server : servers){
			System.out.println("New Record:::::::::::::::::::::");
			WebLogicMonitor vm = new WebLogicMonitor();
			vm.setEnvironment(envName);
			vm.setName(server);
			vm.setState("UNREACHABLE");
			vm.setHealth("HEALTH_NOT_OK");
			vm.setStatus("DOWN");
			vm.setTimestamp(System.currentTimeMillis());
			result.add(vm);
		}
		if (result.size()>0) wLMonitorRepository.save(result);
		return result;
	}

	
  private boolean isNewData(String envName, String name, String state, String health) {
	  return wLMonitorRepository.findVmonitorApplicationExist(envName,  name, state, health) == null;
	}

// join a base url to another path or paths - this will handle trailing or non-trailing /'s
    public static String joinURL(String base, String... paths) throws MalformedURLException {
        StringBuilder result = new StringBuilder(base);
        for (String path : paths) {
            String p = path.replaceFirst("^(\\/)+", "");
            if (result.lastIndexOf("/") != result.length() - 1) {
                result.append('/');
            }
            result.append(p);
        }
        return result.toString();
    }
    
    private JSONArray getJsonArray(JSONObject json, String key) {
        Object array = json.get(key);
        return array == null ? new JSONArray() : (JSONArray) array;
    }


    protected ResponseEntity<String> makeRestCall(String sUrl) throws MalformedURLException,
			SocketTimeoutException, ResourceAccessException {
        URI thisuri = URI.create(sUrl);
        String userInfo = thisuri.getUserInfo();
        //get userinfo from URI or settings (in spring properties)
        if (StringUtils.isEmpty(userInfo) && (this.wLMonitorSettings.getUsername() != null) && (this.wLMonitorSettings.getPassword() != null)) {
            userInfo = this.wLMonitorSettings.getUsername() + ":" + this.wLMonitorSettings.getPassword();
        }
        // Basic Auth only.
        if (StringUtils.isNotEmpty(userInfo)) {
        	return rest.exchange(thisuri, HttpMethod.GET,
                    new HttpEntity<>(createHeaders(userInfo)),
                    String.class);
        } else {
            return rest.exchange(thisuri, HttpMethod.GET, null,
                    String.class);
        }

    }

    protected HttpHeaders createHeaders(final String userInfo) {
        byte[] encodedAuth = Base64.encodeBase64(
                userInfo.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.ACCEPT,"application/json, text/javascript, */*; q=0.01");
        return headers;
    }
    
    
	@Override
	public List<WebLogicMonitor> getHealthInfoFromVpriceDb(String envName,String appName) {
		   List<WebLogicMonitor> environmentComponentList= new ArrayList<WebLogicMonitor>();		  
		   return environmentComponentList;
	}

	@Override
	public List<WLMonitorCollectorItem> getApplications(WLMonitorCollector collector) {
		  List<WLMonitorCollectorItem> wLMonitorCollectorItemList = wLMonitorApplicationRepository.findAllApps(collector.getId());
		  return wLMonitorCollectorItemList;		
		}
	
}
