package com.capitalone.dashboard.model;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Document(collection="cmdb")
public class Cmdb extends BaseModel{

    private ObjectId collectorItemId;
    private long timestamp;

    /**
     * configurationItem unique system generated id
     */
    @NotNull
    private String configurationItem;
    /**
     * configurationItemSubType SubType of the CI
     */
    private String configurationItemSubType;
    /**
     * configurationItemType Type of the CI
     */
    private String configurationItemType;
    private String assignmentGroup;
    /**
     * A owner assigned to the CI usually the id associated with the owner when provided
     */
    private String appServiceOwner;
    /**
     * A owner assigned to the CI usually the id associated with the owner when provided
     */
    private String businessOwner;
    /**
     * A owner assigned to the CI usually the id associated with the owner when provided
     */
    private String supportOwner;
    /**
     * A owner assigned to the CI usually the id associated with the owner when provided
     */
    private String developmentOwner;
    /**
     * A owner assigned to the CI usually the fullName of the owner when provided
     */
    private String appServiceOwnerUserName;
    /**
     * A owner assigned to the CI usually the fullName of the owner when provided
     */
    private String businessOwnerUserName;
    /**
     * A owner assigned to the CI usually the fullName of the owner when provided
     */
    private String supportOwnerUserName;
    /**
     * A owner assigned to the CI usually the fullName of the owner when provided
     */
    private String developmentOwnerUserName;
    /**
     * ownerDept is the upper level department associated with the CI
     */
    private String ownerDept;
    /**
     * ownerSubDept is used in correlation with the ownerDept as a sub department of the ownerDept
     */
    private String ownerSubDept;
    /**
     * commonName Human readable value of the configurationItem
     */
    @NotNull
    private String commonName;
    /**
     * itemType Hygieia specific value for determining if the is an APP or a COMP
     */
    private String itemType;
    /**
     * validConfigItem used to set the validity of the CI
     */
    private boolean validConfigItem;
    /**
     * components used as a way to type relationships to a CI
     */
    private List<String> components;
    private List<String> environments;

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

    public String getOwnerSubDept() {
        return ownerSubDept;
    }

    public void setOwnerSubDept(String ownerSubDept) {
        this.ownerSubDept = ownerSubDept;
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
                this.ownerSubDept,
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
                && Objects.equals(this.validConfigItem, other.validConfigItem)
                && Objects.equals(this.ownerSubDept, other.ownerSubDept)
                && Objects.equals(this.commonName, other.commonName);

    }
}