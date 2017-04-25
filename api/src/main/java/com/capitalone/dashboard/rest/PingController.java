package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.auth.AuthProperties;
import com.capitalone.dashboard.model.AuthType;

@PropertySource(ignoreResourceNotFound=true,value= "classpath:version.properties")

@RestController
public class PingController {

    @Value("${version.number}")
    private String versionNumber;
    
    private AuthProperties authProperties;

    @Autowired
    public PingController(AuthProperties authProperties) {
    	this.authProperties = authProperties;
    }

    @RequestMapping(value = "/ping", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> ping () {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(true);
    }

    @RequestMapping(value = "/appinfo", method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getAppInfo() {
        String appVersion=versionNumber;
        return ResponseEntity.status(HttpStatus.OK).body(appVersion);
    }
    
    @RequestMapping(value = "/authType", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<AuthType> getAuthTypes() {
        return authProperties.getAuthTypes();
    }
}
