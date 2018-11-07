package com.capitalone.dashboard.model.webhook.github;

import com.capitalone.dashboard.misc.HygieiaException;

import java.net.MalformedURLException;
import java.net.URL;

public class GitHubParsed {
    private String url;
    private String apiUrl;
    private String baseApiUrl;
    private String graphQLUrl;
    private String orgName;
    private String repoName;

    private static final String SEGMENT_API = "/api/v3/repos";
    private static final String BASE_API = "/api/v3/";
    private static final String PUBLIC_GITHUB_BASE_API = "api.github.com/";
    private static final String PUBLIC_GITHUB_REPO_HOST = "api.github.com/repos";
    private static final String PUBLIC_GITHUB_HOST_NAME = "github.com";

    private static final String SEGMENT_GRAPHQL = "/api/graphql";
    private static final String PUBLIC_GITHUB_GRAPHQL = "api.github.com/graphql";



    public GitHubParsed(String url) throws MalformedURLException, HygieiaException {
        this.url = url;
        parse();
    }

    private void parse() throws MalformedURLException, HygieiaException {
        if (url.endsWith(".git")) {
            url = url.substring(0, url.lastIndexOf(".git"));
        }
        URL u = new URL(url);
        String host = u.getHost();
        String protocol = u.getProtocol();
        String path = u.getPath();
        String[] parts = path.split("/");
        if (parts.length < 3) {
            throw new HygieiaException("Bad github repo URL: " + url, HygieiaException.BAD_DATA);
        }
        orgName = parts[1];
        repoName = parts[2];
        if (host.startsWith(PUBLIC_GITHUB_HOST_NAME)) {
            baseApiUrl = protocol + "://" + PUBLIC_GITHUB_BASE_API;
            apiUrl = protocol + "://" + PUBLIC_GITHUB_REPO_HOST + path;
            graphQLUrl = protocol + "://" + PUBLIC_GITHUB_GRAPHQL;
        } else {
            apiUrl = protocol + "://" + host + SEGMENT_API + path;
            baseApiUrl = protocol + "://" + host + BASE_API;
            graphQLUrl = protocol + "://" + host + SEGMENT_GRAPHQL;
        }
    }

    public String getUrl() {
        return url;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getBaseApiUrl() {
        return baseApiUrl;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getGraphQLUrl() {
        return graphQLUrl;
    }
}
