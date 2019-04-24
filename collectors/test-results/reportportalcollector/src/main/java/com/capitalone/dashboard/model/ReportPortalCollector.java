package com.capitalone.dashboard.model;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportPortalCollector extends Collector {
    private List<String> sonarServers = new ArrayList<>();
    private List<Double> sonarVersions = new ArrayList<>();
    private List<String> sonarMetrics = new ArrayList<>();
    private List<String> niceNames = new ArrayList<>();
    private static final String NICE_NAME = "niceName";
    private static final String PROJECT_NAME = "options.projectName";

    public List<String> getReportPortalServers() {
        return sonarServers;
    }
   



    public List<String> getNiceNames() {
        return niceNames;
    }

    public void setNiceNames(List<String> niceNames) {
        this.niceNames = niceNames;
    }

    public static ReportPortalCollector prototype(List<String> servers, List<Double> versions, List<String> metrics,List<String> niceNames) {
        ReportPortalCollector protoType = new ReportPortalCollector();
        protoType.setName("ReportPortal");
        protoType.setCollectorType(CollectorType.TestResult);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        if(servers!=null) {
            protoType.getReportPortalServers().addAll(servers);
        }
       
        
        if (!CollectionUtils.isEmpty(niceNames)) {
            protoType.getNiceNames().addAll(niceNames);
        }

        Map<String, Object> allOptions = new HashMap<>();
        allOptions.put(ReportPortalProject.INSTANCE_URL,"");
        allOptions.put(ReportPortalProject.PROJECT_NAME,"");
        allOptions.put(ReportPortalProject.PROJECT_ID, "");
        protoType.setAllFields(allOptions);

        Map<String, Object> uniqueOptions = new HashMap<>();
        uniqueOptions.put(ReportPortalProject.INSTANCE_URL,"");
        uniqueOptions.put(ReportPortalProject.PROJECT_NAME,"");
        protoType.setUniqueFields(uniqueOptions);
        protoType.setSearchFields(Arrays.asList(PROJECT_NAME,NICE_NAME));
        return protoType;
    }
}
