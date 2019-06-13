package com.capitalone.dashboard.model;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SonarCollector extends Collector {
    private List<String> sonarServers = new ArrayList<>();
    private List<String> niceNames = new ArrayList<>();
    private static final String NICE_NAME = "niceName";
    private static final String PROJECT_NAME = "options.projectName";

    public List<String> getSonarServers() {
        return sonarServers;
    }

    public List<String> getNiceNames() {
        return niceNames;
    }

    public void setNiceNames(List<String> niceNames) {
        this.niceNames = niceNames;
    }

    public static SonarCollector prototype(List<String> servers, List<String> niceNames) {
        SonarCollector protoType = new SonarCollector();
        protoType.setName("Sonar");
        protoType.setCollectorType(CollectorType.CodeQuality);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        if(servers!=null) {
            protoType.getSonarServers().addAll(servers);
        }

        if (!CollectionUtils.isEmpty(niceNames)) {
            protoType.getNiceNames().addAll(niceNames);
        }

        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put(SonarProject.INSTANCE_URL,"");
        allOptions.put(SonarProject.PROJECT_NAME,"");
        allOptions.put(SonarProject.PROJECT_ID, "");
        protoType.setAllFields(allOptions);

        Map<String, Object> uniqueOptions = new HashMap<>();
        uniqueOptions.put(SonarProject.INSTANCE_URL,"");
        uniqueOptions.put(SonarProject.PROJECT_NAME,"");
        protoType.setUniqueFields(uniqueOptions);
        protoType.setSearchFields(Arrays.asList(PROJECT_NAME,NICE_NAME));
        return protoType;
    }
}
