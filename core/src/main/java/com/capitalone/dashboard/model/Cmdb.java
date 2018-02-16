package com.capitalone.dashboard.model;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection="cmdb")
public class Cmdb {
    @Id
    private ObjectId id;
    private ObjectId collectorItemId;
    private long timestamp;
    private String configurationItem;
    private String configurationItemSubType;
    private String configurationItemType;
    private String assignmentGroup;
    private String appServiceOwner;
    private String businessOwner;
    private String supportOwner;
    private String developmentOwner;
    private String appServiceOwnerUserName;
    private String businessOwnerUserName;
    private String supportOwnerUserName;
    private String developmentOwnerUserName;
    private String ownerDept;
    private String commonName;
    private String itemType;
    private boolean validConfigItem;
    private List<String> components;

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

    public void setTimestamp(String timestamp) {
        this.timestamp = Long.parseLong(timestamp);
    }

    public String getConfigurationItem() {
        return configurationItem;
    }

    public void setConfigurationItem(String configurationItem) {
        this.configurationItem = configurationItem;
    }

    public String getConfigurationItemSubType() {
        return configurationItemSubType;
    }

    public void setConfigurationItemSubType(String configurationItemSubType) {
        this.configurationItemSubType = configurationItemSubType;
    }

    public String getConfigurationItemType() {
        return configurationItemType;
    }

    public void setConfigurationItemType(String configurationItemType) {
        this.configurationItemType = configurationItemType;
    }

    public String getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setAssignmentGroup(String assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public String getAppServiceOwner() {
        return appServiceOwner;
    }

    public void setAppServiceOwner(String appServiceOwner) {
        this.appServiceOwner = appServiceOwner;
    }

    public String getBusinessOwner() {
        return businessOwner;
    }

    public void setBusinessOwner(String businessOwner) {
        this.businessOwner = businessOwner;
    }

    public String getSupportOwner() {
        return supportOwner;
    }

    public void setSupportOwner(String supportOwner) {
        this.supportOwner = supportOwner;
    }

    public String getDevelopmentOwner() {
        return developmentOwner;
    }

    public void setDevelopmentOwner(String developmentOwner) {
        this.developmentOwner = developmentOwner;
    }

    public String getOwnerDept() {
        return ownerDept;
    }

    public void setOwnerDept(String ownerDept) {
        this.ownerDept = ownerDept;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getAppServiceOwnerUserName() {
        return appServiceOwnerUserName;
    }

    public void setAppServiceOwnerUserName(String appServiceOwnerUserName) {
        this.appServiceOwnerUserName = appServiceOwnerUserName;
    }

    public String getBusinessOwnerUserName() {
        return businessOwnerUserName;
    }

    public void setBusinessOwnerUserName(String businessOwnerUserName) {
        this.businessOwnerUserName = businessOwnerUserName;
    }

    public String getSupportOwnerUserName() {
        return supportOwnerUserName;
    }

    public void setSupportOwnerUserName(String supportOwnerUserName) {
        this.supportOwnerUserName = supportOwnerUserName;
    }

    public String getDevelopmentOwnerUserName() {
        return developmentOwnerUserName;
    }

    public void setDevelopmentOwnerUserName(String developmentOwnerUserName) {
        this.developmentOwnerUserName = developmentOwnerUserName;
    }


    public boolean isValidConfigItem() {
        return validConfigItem;
    }

    public void setValidConfigItem(boolean validConfigItem) {
        this.validConfigItem = validConfigItem;
    }

    public void setComponents(List<String> components) { this.components = components; }

    public List<String> getComponents(){ return components; }

    public boolean isCmdbMatch(Object compareTo){
        boolean doesEqual = true;

        if(compareTo == null || !compareTo.getClass().isAssignableFrom(Cmdb.class)){
            doesEqual = false;
        }else {
            Cmdb newCmdb = (Cmdb) compareTo;

            if((newCmdb.getConfigurationItem() !=null && !newCmdb.getConfigurationItem().equals(configurationItem)) ||
                    (newCmdb.getAssignmentGroup() !=null && !newCmdb.getAssignmentGroup().equals(assignmentGroup)) ||
                    (newCmdb.getAppServiceOwner() !=null && !newCmdb.getAppServiceOwner().equals(appServiceOwner)) ||
                    (newCmdb.getBusinessOwner() !=null && !newCmdb.getBusinessOwner().equals(businessOwner)) ||
                    (newCmdb.getSupportOwner() !=null && !newCmdb.getSupportOwner().equals(supportOwner)) ||
                    (newCmdb.getDevelopmentOwner() !=null && !newCmdb.getDevelopmentOwner().equals(developmentOwner)) ||
                    (newCmdb.getOwnerDept() !=null && !newCmdb.getOwnerDept().equals(ownerDept)) ||
                    (newCmdb.getConfigurationItemSubType() !=null && !newCmdb.getConfigurationItemSubType().equals(configurationItemSubType)) ||
                    (newCmdb.getConfigurationItemType() !=null && !newCmdb.getConfigurationItemType().equals(configurationItemType)) ||
                    (newCmdb.isValidConfigItem() != validConfigItem)){
                doesEqual = false;
            }
        }

        return doesEqual;
    }
}