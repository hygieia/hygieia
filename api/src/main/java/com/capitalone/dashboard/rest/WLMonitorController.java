package com.capitalone.dashboard.rest;


import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capitalone.dashboard.model.WebLogicMonitor;
import com.capitalone.dashboard.request.WeblogicRequest;
import com.capitalone.dashboard.service.WLMonitorService;

@RestController
public class WLMonitorController {

    private static final String JSON = MediaType.APPLICATION_JSON_VALUE;

    private final WLMonitorService wLMonitorService;

    @Autowired
    public WLMonitorController(WLMonitorService wLMonitorService) {
        this.wLMonitorService = wLMonitorService;
    }
    @RequestMapping(value = "/getAllServersByEnvName/{envName}", method = GET, produces = JSON)
    public List<WebLogicMonitor> getAllServersByEnvName(@PathVariable String envName) {
        return (List<WebLogicMonitor>) wLMonitorService.getAllServersByEnvName(envName);
    }
    @RequestMapping(value = "/wlmonitor/widget/{componentId}", method = POST, consumes = JSON)
    public int saveDeployment(@RequestBody WeblogicRequest request,@PathVariable ObjectId componentId) {
        int i = wLMonitorService.addEnvironments(request.getCollectorItemIds());
        wLMonitorService.associateCollectorToComponent(componentId, request.getSelectedCollectorItemIds());
        return i;
    }
}