package com.capitalone.dashboard.cloudwatch.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevegal on 17/06/2018.
 */
public class CloudWatchJob {

    private List<Series> series = new ArrayList<>();
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void addSeries(Series series) {
        this.series.add(series);
    }
}
