package com.capitalone.dashboard.rest;

import com.capitalone.dashboard.model.SystemConfigResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.service.SystemConfigService;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Service to expose system wide configuration settings
 */
@RestController
public class SystemConfigController {

	private final SystemConfigService service;

	@Autowired
	public SystemConfigController(SystemConfigService service) {
		this.service = service;
	}

	@RequestMapping(value = "/config", method = GET, produces = APPLICATION_JSON_VALUE)
	public SystemConfigResponse getSystemConfig() {
		return service.getSystemConfig();
	}
}
