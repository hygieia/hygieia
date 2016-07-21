package com.capitalone.dashboard.model;


public class AppdynamicsApplication extends CollectorItem {
    private static final String APP_NAME = "appName";
    private static final String APP_ID = "appID";
    private static final String APP_DESC = "appDesc";


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

    public String getAppDesc() {
        return (String) getOptions().get(APP_DESC);
    }

    public void setAppDesc (String desc) {
        getOptions().put(APP_DESC, desc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AppdynamicsApplication app = (AppdynamicsApplication) o;

        return getAppID().equals(app.getAppID());
    }

    @Override
    public int hashCode() {
        return getAppID().hashCode();
    }
}
