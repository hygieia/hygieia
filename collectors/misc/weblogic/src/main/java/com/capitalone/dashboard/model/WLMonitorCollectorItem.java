package com.capitalone.dashboard.model;

public class WLMonitorCollectorItem extends CollectorItem {
	
	private static final String ENV_NAME = "environmentName";
	private static final String SERVE_NAME = "serverName";

    public String getEnvironmentName() {
        return (String) getOptions().get(ENV_NAME);
    }

    public void setEnvironmentName(String name) {
        getOptions().put(ENV_NAME, name);
    }
    public String getServerName() {
        return (String) getOptions().get(SERVE_NAME);
    }

    public void setServerName(String serverName) {    	
    		getOptions().put(SERVE_NAME, serverName);
    }
}