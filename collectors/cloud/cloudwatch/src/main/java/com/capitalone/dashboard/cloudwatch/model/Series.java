package com.capitalone.dashboard.cloudwatch.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevegal on 17/06/2018.
 */
public class Series {

    private List<String> logStreams = new ArrayList<>();
    private String filter;
    private String name;
    private String logGroupName;
    private String filterPattern;

    public void setName(String name) {
        this.name = name;
    }

    public void setLogGroupName(String logGroupName) {
        this.logGroupName = logGroupName;
    }

    public void addLogStream(String stream) {
        this.logStreams.add(stream);
    }

    public void setFilterPattern(String filterPattern) {
        this.filterPattern = filterPattern;
    }
}
