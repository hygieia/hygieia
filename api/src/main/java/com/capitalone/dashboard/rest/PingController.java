package com.capitalone.dashboard.rest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@PropertySource(ignoreResourceNotFound=true,value= "classpath:version.properties")

@RestController
public class PingController {
	private static final Logger LOGGER = Logger.getLogger(PingController.class);

    @Value("${version.number}")
    private String versionNumber;

    public PingController() {

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
    
    @RequestMapping(value = "/findUser", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Boolean> authenticatedPing() {
		LOGGER.debug("===> finduserMethod");
		return ResponseEntity.status(HttpStatus.OK).body(true);
	}
}
