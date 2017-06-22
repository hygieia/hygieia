package com.capitalone.dashboard.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.CaApm;
import com.capitalone.dashboard.service.CaApmService;
@RestController
public class CaApmController {

    private final CaApmService caApmService;

    private static final String JSON = MediaType.APPLICATION_JSON_VALUE;

    @Autowired
    public CaApmController(CaApmService caApmService) {
        this.caApmService = caApmService;
    }
    @RequestMapping(value = "/getAlertsByManageModuleName/{moduleName}", method = GET, produces = JSON)
	public List<CaApm> getAlertsByManageModuleName(@PathVariable String moduleName) {
			return (List<CaApm>) caApmService.getAlertsByManageModuleName(moduleName);
	}
    
}