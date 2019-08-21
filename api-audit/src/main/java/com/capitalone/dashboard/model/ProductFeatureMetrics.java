package com.capitalone.dashboard.model;



import java.util.List;

/**
 * Quality metrics data model of an application
 */

public class ProductFeatureMetrics {
    
    private String id;
    
    private String name;

    private String type;

    private String lob;
    
    private Double percentage;
    
    private Double codeCoverage;

    private Double perfErrorRate;
    

    private Double featureTestPass;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
    
    private List<ComponentFeatureMetrics> components;

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

    public String getLob() {
        return lob;
    }

    public void setLob(String lob) {
        this.lob = lob;
    }

    public Double getPercentage() { return percentage; }

    public void setPercentage(Double percentage) { this.percentage = percentage; }

    public Double getCodeCoverage() {
        return codeCoverage;
    }


    public void setCodeCoverage(Double codeCoverage) {
        this.codeCoverage = codeCoverage;
    }

    public Double getPerfErrorRate() {
        return perfErrorRate;
    }

    public void setPerfErrorRate(Double perfErrorRate) {
        this.perfErrorRate = perfErrorRate;
    }

    public Double getFeatureTestPass() {
        return featureTestPass;
    }

    public void setFeatureTestPass(Double featureTestPass) {
        this.featureTestPass = featureTestPass;
    }
    

    public List<ComponentFeatureMetrics> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentFeatureMetrics> components) {
        this.components = components;
    }



}
