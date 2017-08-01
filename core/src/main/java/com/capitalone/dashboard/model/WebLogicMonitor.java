package com.capitalone.dashboard.model;

import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "weblogic_monitor")
public class WebLogicMonitor extends BaseModel {

    private String	 environment;
    private String	 name;
    private String	 state;
    private String   status; //to store binary value up/down
    private String	 health;
    private long 	timestamp;

    public String getEnvironment() {
        return environment;
    }
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getHealth() {
        return health;
    }
    public void setHealth(String health) {
        this.health = health;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}