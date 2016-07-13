package com.capitalone.dashboard.model;

import java.util.Date;

/**
 * CollectorItem extension to store the github repo url and branch.
 */
public class AppdynamicsApplication extends CollectorItem {
    private static final String APP_NAME = "appName"; // http://github.company.com/jack/somejavacode
    private static final String APP_ID = "appID"; // master, development etc.
    private static final String APP_URL = "url";
    private static final String LAST_UPDATE_TIME = "lastUpdate";



    public String getAppName() {
        return (String) getOptions().get(APP_NAME);
    }

    public void setAppName(String name) {
        getOptions().put(APP_NAME, name);
    }

    public String getAppID() {
        return (String) getOptions().get(APP_ID);
    }

    public void setAppID(String id) {
        getOptions().put(APP_ID, id);
    }

    public String getAppUrl() {
        return (String) getOptions().get(APP_URL);
    }

    public void setAppUrl(String url) {
        getOptions().put(APP_URL, url);
    }


    public Date getLastUpdateTime() {
        Object latest = getOptions().get(LAST_UPDATE_TIME);
        return (Date) latest;
    }

    public void setLastUpdateTime(Date date) {
        getOptions().put(LAST_UPDATE_TIME, date);
    }

}
