package com.capitalone.dashboard.collector;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bean to hold settings specific to the developer datasource .
 */
@Component
@ConfigurationProperties(prefix = "developer")
public class DeveloperDataSettings {

    //Add as many as you like - you can add DB Urls, credentials etc
    //For my initial use case, I am using this for accessing a live REST api
    //to get DeveloperData
    private String url;
    private String password;
    private String impersonateUserId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImpersonateUserId() {
        return impersonateUserId;
    }

    public void setImpersonateUserId(String impersonateUserId) {
        this.impersonateUserId = impersonateUserId;
    }


}
