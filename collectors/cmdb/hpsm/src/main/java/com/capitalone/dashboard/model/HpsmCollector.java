package com.capitalone.dashboard.model;

import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * CollectorItem extension to store the github repo url and branch.
 */
public class HpsmCollector extends CollectorItem {

    private String appName;
    private String componentName;
    private String serOwner;
    private String supOwner;
    private String devOwner;
    private String busOwner;

    public String getAppName() {
        return appName;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getSerOwner() {
        return serOwner;
    }

    public void setSerOwner(String serOwner) {
        this.serOwner = serOwner;
    }

    public String getSupOwner() {
        return supOwner;
    }

    public void setSupOwner(String supOwner) {
        this.supOwner = supOwner;
    }

    public String getDevOwner() {
        return devOwner;
    }

    public void setDevOwner(String devOwner) {
        this.devOwner = devOwner;
    }

    public String getBusOwner() {
        return busOwner;
    }

    public void setBusOwner(String busOwner) {
        this.busOwner = busOwner;
    }

}
