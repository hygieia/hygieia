package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

public class CmdbRequest {
    @NotNull
    private String configurationItem;
    @NotNull
    private String configurationItemSubType;
    @NotNull
    private String configurationItemType;
    @NotNull
    private String assignmentGroup;
    @NotNull
    private String ownerDept;
    @NotNull
    private String commonName;
    @NotNull
    private String toolName;

    private String configurationItemBusServName;

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

    public String getConfigurationItemBusServName() {
        return configurationItemBusServName;
    }

    public void setConfigurationItemBusServName(String configurationItemBusServName) {
        this.configurationItemBusServName = configurationItemBusServName;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }
}
