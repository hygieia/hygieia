package com.capitalone.dashboard.model;


public class PivotalTrackerCollector extends Collector {
	private String apiToken;

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}
