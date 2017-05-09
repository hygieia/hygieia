package com.capitalone.dashboard.model;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="cmdb")
public class Cmdb {
    @Id
    private ObjectId id;
    private ObjectId collectorItemId;
    private long timestamp;
    private String ConfigurationItem;
    private String AssignmentGroup;
    private String AppServiceOwner;
    private String BusinessOwner;
    private String SupportOwner;
    private String DevelopmentOwner;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

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

    public String getConfigurationItem() {
        return ConfigurationItem;
    }

    public void setConfigurationItem(String configurationItem) {
        ConfigurationItem = configurationItem;
    }

    public String getAssignmentGroup() {
        return AssignmentGroup;
    }

    public void setAssignmentGroup(String assignmentGroup) {
        AssignmentGroup = assignmentGroup;
    }

    public String getAppServiceOwner() {
        return AppServiceOwner;
    }

    public void setAppServiceOwner(String appServiceOwner) {
        AppServiceOwner = appServiceOwner;
    }

    public String getBusinessOwner() {
        return BusinessOwner;
    }

    public void setBusinessOwner(String businessOwner) {
        BusinessOwner = businessOwner;
    }

    public String getSupportOwner() {
        return SupportOwner;
    }

    public void setSupportOwner(String supportOwner) {
        SupportOwner = supportOwner;
    }

    public String getDevelopmentOwner() {
        return DevelopmentOwner;
    }

    public void setDevelopmentOwner(String developmentOwner) {
        DevelopmentOwner = developmentOwner;
    }
}
