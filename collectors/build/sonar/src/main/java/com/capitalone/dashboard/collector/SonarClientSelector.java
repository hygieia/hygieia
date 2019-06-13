package com.capitalone.dashboard.collector;


import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.util.Supplier;

@Component
public class SonarClientSelector {
	private static final Log LOG = LogFactory.getLog(SonarClientSelector.class);

	private static final String URL_VERSION_RESOURCE = "/api/server/version";

    private DefaultSonar6Client sonar6Client;
    private DefaultSonar56Client sonar56Client;
    private DefaultSonarClient sonarClient;
    private RestOperations rest;
    
    @Autowired
    public SonarClientSelector(
    		DefaultSonar6Client sonar6Client, DefaultSonar56Client sonar56Client,
            @Qualifier("DefaultSonarClient") DefaultSonarClient sonarClient,
            Supplier<RestOperations> restOperationsSupplier) {

        this.sonar6Client = sonar6Client;
        this.sonar56Client = sonar56Client;
        this.sonarClient = sonarClient;
        this.rest = restOperationsSupplier.get();
    }
    
    public Double getSonarVersion(String instanceUrl){
    	Double version = 6.0;
    	try {
    	    ResponseEntity<String> versionResponse = rest.exchange(URI.create(instanceUrl + URL_VERSION_RESOURCE), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
    	    if(!versionResponse.getBody().isEmpty()) {
    	        if(StringUtils.countOccurrencesOf(versionResponse.getBody(), ".") > 1) {
    	            version = Double.parseDouble(versionResponse.getBody().substring(0, 3));
    	        } else {
    	            version = Double.parseDouble(versionResponse.getBody());
    	        }
    	    }    
    	} catch (RestClientException e) {
    		LOG.info("Rest exception occured at fetching sonar version");
    	}
  
        return version;
    }

    public SonarClient getSonarClient(Double version) {
        if(version != null && version == 5.6){
          return sonar56Client;
        }
        return ((version == null) || (version < 6.3)) ? sonarClient : sonar6Client;
    }
}
