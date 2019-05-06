package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Traceability {

    private int totalStoryCount;

    private double threshold;

    private double percentage;

    private List<HashMap> totalStories = new ArrayList<HashMap>();

    private List<String> totalCompletedStories = new ArrayList<>();

    public List<HashMap> getTotalStories() {
        return totalStories;
    }

    public void setTotalStories(List<HashMap> totalStories) { this.totalStories = totalStories; }

    public int getTotalStoryCount() {
        return totalStoryCount;
    }

    public void setTotalStoryCount(int totalStoryCount) {
        this.totalStoryCount = totalStoryCount;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public List<String> getTotalCompletedStories() {
        return totalCompletedStories;
    }

    public void setTotalCompletedStories(List<String> totalCompletedStories) {
        this.totalCompletedStories = totalCompletedStories;
    }
}
