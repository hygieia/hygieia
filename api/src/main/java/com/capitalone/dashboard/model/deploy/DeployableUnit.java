package com.capitalone.dashboard.model.deploy;

import com.capitalone.dashboard.model.EnvironmentComponent;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class DeployableUnit {
    private final String name;
    private final String version;
    private final String jobUrl;
    private final boolean deployed;
    private final long lastUpdated;
    private final List<Server> servers = new ArrayList<>();

    public DeployableUnit(EnvironmentComponent component, Iterable<Server> servers) {
        this.name = component.getComponentName();
        this.version = component.getComponentVersion();
        this.deployed = component.isDeployed();
        this.jobUrl = component.getJobUrl();
        this.lastUpdated = component.getAsOfDate();
        this.servers.addAll(Lists.newArrayList(servers));
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public boolean isDeployed() {
        return deployed;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public List<Server> getServers() {
        return servers;
    }
}
