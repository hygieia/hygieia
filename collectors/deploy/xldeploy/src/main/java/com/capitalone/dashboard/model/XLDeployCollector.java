package com.capitalone.dashboard.model;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collector implementation for XLDeploy that stores XLDeploy server URLs.
 */
public class XLDeployCollector extends Collector {
    private List<String> xldeployServers = new ArrayList<>();
    private List<String> niceNames = new ArrayList<>();

    public List<String> getXLdeployServers() {
        return xldeployServers;
    }
    
    public List<String> getNiceNames() {
    	return niceNames;
    }

    public static XLDeployCollector prototype(List<String> servers, List<String> niceNames) {
    	XLDeployCollector protoType = new XLDeployCollector();
        protoType.setName("XLDeploy");
        protoType.setCollectorType(CollectorType.Deployment);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        protoType.getXLdeployServers().addAll(servers);
        if (!CollectionUtils.isEmpty(niceNames)) {
            protoType.getNiceNames().addAll(niceNames);
        }

        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put(XLDeployApplication.INSTANCE_URL,"");
        allOptions.put(XLDeployApplication.APP_NAME,"");
        allOptions.put(XLDeployApplication.APP_ID, "");
        allOptions.put(XLDeployApplication.APP_TYPE, "");
        protoType.setAllFields(allOptions);

        Map<String, Object> uniqueOptions = new HashMap<>();
        uniqueOptions.put(XLDeployApplication.INSTANCE_URL,"");
        uniqueOptions.put(XLDeployApplication.APP_NAME,"");
        protoType.setUniqueFields(uniqueOptions);


        return protoType;
    }
}
