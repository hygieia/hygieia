package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

public class AwsStatusDataCreateRequest {
    @NotNull
    private String name;
    private String url;

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    public String getUrl() { return this.url; }

    public void setUrl(String url) { this.url = url; }
}
