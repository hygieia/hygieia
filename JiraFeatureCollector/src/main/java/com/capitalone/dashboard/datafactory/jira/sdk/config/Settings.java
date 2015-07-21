package com.capitalone.dashboard.datafactory.jira.sdk.config;

import com.capitalone.dashboard.datafactory.jira.sdk.util.Supplier;

import java.util.Properties;

/**
 * Reads settings from Properties
 */
public class Settings {

    private final Properties properties;
    
    private static final String PROP_JIRA_BASE_URL = "jira.base.url";
    private static final String PROP_JIRA_QUERY_ENDPOINT = "jira.query.endpoint";

    private static final String PROP_JIRA_CREDENTIALS = "jira.credentials";
    
    private static final String PROP_JIRA_AUTHTOKEN = "jira.oauth.authtoken";
    private static final String PROP_JIRA_REFRESHTOKEN = "jira.oauth.refreshtoken";
    private static final String PROP_JIRA_REDIRECTURI = "jira.oauth.redirecturi";
    private static final String PROP_JIRA_EXPIRETIME = "jira.oauth.expiretime";

    private static final String PROP_PROXY_URL = "jira.proxy.url";
    private static final String PROP_PROXY_PORT = "jira.proxy.port";

    public Settings(Supplier<Properties> propertiesSupplier) {
        this.properties = propertiesSupplier.get();
    }

    public String getJiraBaseUrl() {
        return properties.getProperty(PROP_JIRA_BASE_URL);
    }
    
    public String getJiraQueryApiEndpoint() {
        return properties.getProperty(PROP_JIRA_QUERY_ENDPOINT);
    }

    public String getJiraCredentials() {
        return properties.getProperty(PROP_JIRA_CREDENTIALS);
    }
    
    public String getJiraAuthToken() {
        return properties.getProperty(PROP_JIRA_AUTHTOKEN);
    }
    
    public String getJiraRefreshToken() {
        return properties.getProperty(PROP_JIRA_REFRESHTOKEN);
    }
    
    public String getJiraRedirectUri() {
        return properties.getProperty(PROP_JIRA_REDIRECTURI);
    }
    
    public String getJiraExpireTime() {
        return properties.getProperty(PROP_JIRA_EXPIRETIME);
    }
    
    public String getProxyUrl() {
        return properties.getProperty(PROP_PROXY_URL);
    }
    
    public String getProxyPort() {
        return properties.getProperty(PROP_PROXY_PORT);
    }
}
