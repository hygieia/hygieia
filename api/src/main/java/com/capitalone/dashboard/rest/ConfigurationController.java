package com.capitalone.dashboard.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.auth.access.Admin;
import com.capitalone.dashboard.model.Configuration;
import com.capitalone.dashboard.service.ConfigurationService;

@Admin
@RestController
public class ConfigurationController {
	private final ConfigurationService configurationService;
	
	@Autowired
	public ConfigurationController(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
	
	 @RequestMapping(value = "/dashboard/generalConfig", method = PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	    public ResponseEntity<List<Configuration>> insertConfigurationFile(@Valid @RequestBody List<Configuration> config) {
		 return ResponseEntity
                 .status(HttpStatus.CREATED)
                 .body(configurationService.insertConfigurationData(config));
	 }
	 
	 @RequestMapping(value = "/dashboard/generalConfig/fetch", method = GET,produces = APPLICATION_JSON_VALUE)
	 public List<Configuration> getConfigurationFile(){
		 return configurationService.getConfigurationData();
	 }
}
