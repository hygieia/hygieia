package com.capitalone.dashboard.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.service.SystemConfigService;
import com.captialone.dashboard.response.SystemConfigResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Service to expose system wide configuration settings
 * 
 * @author <a href="mailto:MarkRx@users.noreply.github.com">MarkRx</a>
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
