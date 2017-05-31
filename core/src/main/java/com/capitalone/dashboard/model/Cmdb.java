package com.capitalone.dashboard.model;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

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

    @Override
    public int hashCode(){
        return Objects.hash(configurationItem,
                assignmentGroup,
                appServiceOwner,
                businessOwner,
                supportOwner,
                developmentOwner,
                ownerDept,
                itemType,
                configurationItemSubType,
                configurationItemType);
    }
    @Override
    public boolean equals(Object compareTo){
        boolean doesEqual = true;

        if(compareTo == null || !compareTo.getClass().isAssignableFrom(Cmdb.class)){
            doesEqual = false;
        }else {
            Cmdb newCmdb = (Cmdb) compareTo;

            if(!newCmdb.toString().equals(toString())){
                doesEqual = false;
            }
        }

        return doesEqual;
    }

    /**
     *  Returns human readable string of the Cmdb Object.
     *  * equals(Object object) depends on this method. Changing this method could alter the return of the equals method.
     * @return object to string
     */
    @Override
    public String toString() {

        StringBuffer buf = new StringBuffer(210);
        buf.append("configurationItem: ")
                .append(configurationItem)
                .append("\nassignmentGroup: ")
                .append(assignmentGroup)
                .append("\nappServiceOwner: ")
                .append(appServiceOwner)
                .append("\nbusinessOwner: ")
                .append(businessOwner)
                .append("\nsupportOwner: ")
                .append(supportOwner)
                .append("\ndevelopmentOwner: ")
                .append(developmentOwner)
                .append("\nownerDept: ")
                .append(ownerDept)
                .append("\nitemType: ")
                .append(itemType)
                .append("\nconfigurationItemSubType: ")
                .append(configurationItemSubType)
                .append("\nconfigurationItemType: ")
                .append(configurationItemType)
                .append("\nvalidConfigItem: ")
                .append(validConfigItem);

        return buf.toString();
    }
}