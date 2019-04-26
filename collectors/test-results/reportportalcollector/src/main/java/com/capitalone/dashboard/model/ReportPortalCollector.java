package com.capitalone.dashboard.model;

import org.springframework.util.CollectionUtils;

import com.capitalone.dashboard.collector.ReportPortalSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportPortalCollector extends Collector {
    private List<String> Servers = new ArrayList<>();
    private List<String> niceNames = new ArrayList<>();
    private String projectName;
    private static final String NICE_NAME = "niceName";
    private static final String PROJECT_NAME = "options.projectName";

    public List<String> getReportPortalServers() {
        return Servers;
    }
   


    public String getProjectName() {
    	return projectName;
    }
    
    public void setProjectName(String projectName) {
    	this.projectName=projectName;
    }
    public List<String> getNiceNames() {
        return niceNames;
    }

    public void setNiceNames(List<String> niceNames) {
        this.niceNames = niceNames;
    }

    public static ReportPortalCollector prototype(ReportPortalSettings reportSettings) {
        ReportPortalCollector protoType = new ReportPortalCollector();
        protoType.setName("reportportal");
        protoType.setCollectorType(CollectorType.TestResult);
        protoType.setOnline(true);
        protoType.setEnabled(true);
        List<String> servers=reportSettings.getServers();
        List<String> niceNames=reportSettings.getNiceNames();
        //String ProjectName=reportSettings.getProjectName();
        protoType.setProjectName(reportSettings.getProjectName());
        if(servers!=null) {
            protoType.getReportPortalServers().addAll(servers);
        }
       
        
        if (!CollectionUtils.isEmpty(niceNames)) {
            protoType.getNiceNames().addAll(niceNames);
        }

        Map<String, Object> allOptions = new HashMap<>();
        //allOptions.put(ReportPortalProject.INSTANCE_URL,"" );
        allOptions.put(ReportPortalProject.PROJECT_NAME,reportSettings.getProjectName());
        allOptions.put(ReportPortalProject.PROJECT_ID, reportSettings.getProjectName());
        protoType.setAllFields(allOptions);

        Map<String, Object> uniqueOptions = new HashMap<>();
       // uniqueOptions.put(ReportPortalProject.INSTANCE_URL,reportSettings.getInstanceUrl());
        uniqueOptions.put(ReportPortalProject.PROJECT_NAME,reportSettings.getProjectName());
        
        protoType.setUniqueFields(uniqueOptions);
        protoType.setSearchFields(Arrays.asList(PROJECT_NAME,NICE_NAME));
        return protoType;
    }
}
