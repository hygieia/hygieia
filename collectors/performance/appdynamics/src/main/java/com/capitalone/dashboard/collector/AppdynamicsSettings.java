package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Component
@ConfigurationProperties(prefix = "appdynamics")
public class AppdynamicsSettings {
    private String username;//="srvHygieiaPerf";
    private String password;//="Capital1";
    private String account;
    private String cron;//="1 * * * * *";
    private Integer timeWindow = 15; //default to 15 minutes
    private String instanceUrls;//="http://appdyn-hqa-c01.kdc.capitalone.com,http://appdyn-hqa-c01.kdc.capitalone.com,http://appdyn-hqa-c01.kdc.capitalone.com";

    public String getInstanceUrls() {
        return instanceUrls;
    }

    public void setInstanceUrls(String instanceUrls) {
        this.instanceUrls = instanceUrls;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<String> getInstanceUrlList() {

        return Arrays.asList(instanceUrls.split(","));
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

    public String getDashboardUrl(String instanceUrl) {
        String dashboardUrl = instanceUrl + "/controller/#/location=APP_DASHBOARD&timeRange=last_15_minutes.BEFORE_NOW.-1.-1.15&application=%s&dashboardMode=force";
        return dashboardUrl;
    }


    public Integer getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(Integer timeWindow) {
        this.timeWindow = timeWindow;
    }
}
