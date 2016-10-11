package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Collector;

import java.util.List;

/**
 * Collectors get registered with the dashboard and their configuration ends up here.
 */
public class JenkinsCodeQualityCollector extends Collector {


    private List<String> buildServers;

    public List<String> getBuildServers() {
        return buildServers;
    }
}
