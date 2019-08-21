package com.capitalone.dashboard.model;

import java.util.List;

/**
 * Quality metrics data model of an executive
 */

public class ExecutiveFeatureMetrics {

    private String name;

    private String type;

    private String lobName;

    private String percentage;

    private List<ProductFeatureMetrics> applications;

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

    public String getLobName() {
        return lobName;
    }

    public void setLobName(String lobName) {
        this.lobName = lobName;
    }

    public List<ProductFeatureMetrics> getApplications() {
        return applications;
    }

    public void setApplications(List<ProductFeatureMetrics> applications) {
        this.applications = applications;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}
