package com.capitalone.dashboard.cloudwatch.model;

import java.util.ArrayList;
import java.util.List;

public class Series {

    private List<String> logStreams = new ArrayList<>();
    private String name;
    private String logGroupName;
    private String filterPattern;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLogGroupName(String logGroupName) {
        this.logGroupName = logGroupName;
    }

    public String getLogGroupName() {
        return logGroupName;
    }

    public void addLogStream(String stream) {
        this.logStreams.add(stream);
    }

    public List<String> getLogStreams() {
        return logStreams;
    }

    public void setFilterPattern(String filterPattern) {
        this.filterPattern = filterPattern;
    }

    public String getFilterPattern() {
        return filterPattern;
    }
}
