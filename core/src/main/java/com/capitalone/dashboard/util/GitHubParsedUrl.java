package com.capitalone.dashboard.util;

public class GitHubParsedUrl {
    private String url;
    private String host;
    private String apiUrl;
    private String orgName;
    private String repoName;


    public GitHubParsedUrl(String url){
        this.url = url;
        parse();
    }

    private void parse(){
        if (url.endsWith(".git")) {
            url = url.substring(0, url.lastIndexOf(".git"));
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }
}
