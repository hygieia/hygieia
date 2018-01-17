package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document(collection="collitem_cfghist")
public class CollectorItemConfigHistory extends BaseModel {

    private ObjectId collectorItemId;
    private long timestamp;
    private ConfigHistOperationType operation;
    private String userName;
    private String userID;
    private Map<String, Object> changeMap = new HashMap<>();

    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ConfigHistOperationType getOperation() {
        return operation;
    }

    public void setOperation(ConfigHistOperationType operation) {
        this.operation = operation;
    }

    public Map<String, Object> getChangeMap() {
        return changeMap;
    }

    public void setChangeMap(Map<String, Object> changeMap) {
        this.changeMap = changeMap;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
