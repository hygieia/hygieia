package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.AwsStatus;

// Provides additional functionality to the Aws Status model.
public class AwsStatusRequest {
    private String name;
    private String url;
    private int status;

    public String getName() { return this.name;}

    public void setName(String name) { this.name = name; }

    public int getStatus() { return this.status; }

    public void setStatus(int status) { this.status = status; }

    public String getUrl() { return this.url;}

    public void setUrl(String url) { this.url = url; }

    public AwsStatus update (AwsStatus awsStatus) {
        awsStatus.setName(this.name);
        awsStatus.setUrl(this.url);
        awsStatus.setStatus(this.status);
        return awsStatus;
    }
}
