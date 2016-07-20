package com.capitalone.dashboard.collector;

import org.appdynamics.appdrestapi.RESTAccess;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "appdynamics")
public class AppdynamicsSettings {
    // private String cron;
    private String username;
    private String password;
    private String account;
    private boolean useSSL;
    private String controller;
    private String port;
    private String appID;
    private String appName;
    private String cron;


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }


    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * Accessor method for the current chronology setting, for the scheduler
     */
    public String getCron() {
         return cron;
     }

    //TODO: implement users put in own metrics to use

     public void setCron(String cron) {
         this.cron = cron;
     }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RESTAccess getAccess() {

        return new RESTAccess(controller, port, useSSL, username, password, account);
    }

  /*  public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }
    */
}
