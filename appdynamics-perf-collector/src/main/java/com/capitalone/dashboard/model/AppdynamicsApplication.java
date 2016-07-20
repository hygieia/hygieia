package com.capitalone.dashboard.model;


public class AppdynamicsApplication extends CollectorItem {
    private static final String APP_NAME = "appName"; // http://github.company.com/jack/somejavacode
    private static final String APP_ID = "appID"; // master, development etc.

    public AppdynamicsApplication(org.appdynamics.appdrestapi.data.Application app) {
        getOptions().put(APP_ID, app.getId());
        getOptions().put(APP_NAME, app.getName());
    }

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

}
