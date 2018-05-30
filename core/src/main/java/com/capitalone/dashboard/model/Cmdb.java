package com.capitalone.dashboard.model;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Document(collection="cmdb")
public class Cmdb {
    @Id
    private ObjectId id;
    private ObjectId collectorItemId;
    private long timestamp;
    @NotNull
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
    private List<String> environments;

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

    public List<String> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<String> environments) {
        this.environments = environments;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(
                this.configurationItem,
                this.assignmentGroup,
                this.appServiceOwner,
                this.businessOwner,
                this.supportOwner,
                this.developmentOwner,
                this.ownerDept,
                this.itemType,
                this.configurationItemSubType,
                this.configurationItemType);
    }
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Cmdb other = (Cmdb) obj;
        return    Objects.equals(this.configurationItem, other.configurationItem)
                && Objects.equals(this.assignmentGroup, other.assignmentGroup)
                && Objects.equals(this.appServiceOwner, other.appServiceOwner)
                && Objects.equals(this.businessOwner, other.businessOwner)
                && Objects.equals(this.supportOwner, other.supportOwner)
                && Objects.equals(this.developmentOwner, other.developmentOwner)
                && Objects.equals(this.ownerDept, other.ownerDept)
                && Objects.equals(this.configurationItemSubType, other.configurationItemSubType)
                && Objects.equals(this.configurationItemType, other.configurationItemType)
                && Objects.equals(this.validConfigItem, other.validConfigItem);

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