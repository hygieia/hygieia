package com.capitalone.dashboard.cloudwatch.model;

import java.util.ArrayList;
import java.util.List;

public class CloudWatchJob {

    private List<Series> series = new ArrayList<>();
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addSeries(Series series) {
        this.series.add(series);
    }

    public List<Series> getSeries() {
        return series;
    }
}
