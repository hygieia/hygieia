package com.capitalone.dashboard.request;


import com.capitalone.dashboard.config.collector.CloudConfig;
import com.capitalone.dashboard.model.NameValue;

import java.util.ArrayList;
import java.util.List;

public class CloudInstanceAggregateRequest {
    private List<NameValue> tags = new ArrayList<>();
    private List<String> instanceIds = new ArrayList<>();
    private CloudConfig config;

    public List<NameValue> getTags() {
        return tags;
    }

    public List<String> getInstanceIds() {
        return instanceIds;
    }

    public CloudConfig getConfig() {
        return config;
    }

    public void setConfig(CloudConfig config) {
        this.config = config;
    }
}
