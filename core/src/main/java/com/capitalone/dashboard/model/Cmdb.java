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
    private String configurationItem;
    private String assignmentGroup;
    private String appServiceOwner;
    private String businessOwner;
    private String supportOwner;
    private String developmentOwner;
    private String ownerDept;

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

        StringBuffer buf = new StringBuffer();
        buf.append("configurationItem: " + configurationItem);
        buf.append("\nassignmentGroup: " + assignmentGroup);
        buf.append("\nappServiceOwner: " + appServiceOwner);
        buf.append("\nbusinessOwner: " + businessOwner);
        buf.append("\nsupportOwner: " + supportOwner);
        buf.append("\ndevelopmentOwner: " + developmentOwner);
        buf.append("\nownerDept: " + ownerDept);

        return buf.toString();
    }
}