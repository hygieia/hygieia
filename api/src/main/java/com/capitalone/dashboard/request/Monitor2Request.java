package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.Monitor2;

// Provides additional functionality to the Aws Status model.
public class Monitor2Request {
    private String name;
    private String url;
    private int status;

    public String getName() { return this.name;}

    public void setName(String name) { this.name = name; }

    public int getStatus() { return this.status; }

    public void setStatus(int status) { this.status = status; }

    public String getUrl() { return this.url;}

    public void setUrl(String url) { this.url = url; }

    public Monitor2 update (Monitor2 monitor2) {
        monitor2.setName(this.name);
        monitor2.setUrl(this.url);
        monitor2.setStatus(this.status);
        return monitor2;
    }
}
