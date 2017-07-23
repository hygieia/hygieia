package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.CollectionUtils;

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
        protoType.getUniqueFields().addAll(Arrays.asList(XLDeployApplication.INSTANCE_URL,XLDeployApplication.APP_NAME));
        protoType.getAllFields().addAll(Arrays.asList(XLDeployApplication.INSTANCE_URL,XLDeployApplication.APP_NAME, XLDeployApplication.APP_ID, XLDeployApplication.APP_TYPE));
        return protoType;
    }
}
