package com.capitalone.dashboard.model;

import java.util.HashMap;

import java.util.List;

public class FeatureMetrics {

    private String name;

    private String type;

    private String application;

    private String lob;

    private List<HashMap> metrics;

    private String message;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public List<HashMap> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<HashMap> metrics) {
        this.metrics = metrics;
    }

    public String getLob() {
        return lob;
    }

    public void setLob(String lob) {
        this.lob = lob;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

}
