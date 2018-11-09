package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection="incident")
public class Incident extends BaseModel {

    private ObjectId collectorItemId;
    private Long timestamp;
    private String incidentItem;
    private String incidentID;
    private String category;
    private Long openTime;
    private String openedBy;
    private String severity;
    private Long updatedTime;
    private String primaryAssignmentGroup;
    private String status;
    private Long closedTime;
    private String closedBy;
    private String closureCode;
    private String incidentDescription;
    private String incidentSysID;

    private String affectedItem;
    private String affectedItemSysId;
    private String affectedItemDisplayId;
    private String affectedItemType;

    private String affectedBusinessServiceItem;
    private String affectedBusinessServiceItemSysId;
    private String affectedBusinessServiceItemDisplayId;
    private String affectedBusinessServiceItemType;

    private boolean attachedToBusinessServiceOnly;

    public String getAffectedItemType() { return affectedItemType; }

    public void setAffectedItemType(String affectedItemType) { this.affectedItemType = affectedItemType; }

    public String getAffectedBusinessServiceItemType() { return affectedBusinessServiceItemType; }

    public void setAffectedBusinessServiceItemType(String affectedBusinessServiceItemType) {
        this.affectedBusinessServiceItemType = affectedBusinessServiceItemType;
    }

    public boolean isAttachedToBusinessServiceOnly() { return attachedToBusinessServiceOnly; }

    public void setAttachedToBusinessServiceOnly(boolean attachedToBusinessServiceOnly) {
        this.attachedToBusinessServiceOnly = attachedToBusinessServiceOnly;
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

    public ObjectId getCollectorItemId() { return collectorItemId; }

    public void setCollectorItemId(ObjectId collectorItemId) { this.collectorItemId = collectorItemId; }

    public long getTimestamp() { return timestamp; }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public void setTimestamp(String timestamp) { this.timestamp = DATE_FORMATTER.parseMillis(timestamp); }

    public String getIncidentItem() { return incidentItem; }

    public void setIncidentItem(String incidentItem) { this.incidentItem = incidentItem; }

    public String getIncidentID() { return incidentID; }

    public void setIncidentID(String incidentID) { this.incidentID = incidentID; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public Long getOpenTime() { return openTime; }

    public void setOpenTime(Long openTime) { this.openTime = openTime; }

    public void setOpenTime(String openTime) { this.setOpenTime(DATE_FORMATTER.parseMillis(openTime)); }

    public String getOpenedBy() { return openedBy; }

    public void setOpenedBy(String openedBy) { this.openedBy = openedBy; }

    public String getSeverity() { return severity; }

    public void setSeverity(String severity) { this.severity = severity; }

    public long getUpdatedTime() { return updatedTime; }

    public void setUpdatedTime(long updatedTime) { this.updatedTime = updatedTime; }

    public void setUpdatedTime(String updatedTime) { this.updatedTime = DATE_FORMATTER.parseMillis(updatedTime); }

    public String getPrimaryAssignmentGroup() { return primaryAssignmentGroup; }

    public void setPrimaryAssignmentGroup(String primaryAssignmentGroup) { this.primaryAssignmentGroup = primaryAssignmentGroup; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Long getClosedTime() { return closedTime; }

    public void setClosedTime(Long closedTime) { this.closedTime = closedTime; }

    public void setClosedTime(String closedTime) { this.setClosedTime(DATE_FORMATTER.parseMillis(closedTime)); }

    public String getClosedBy() { return closedBy; }

    public void setClosedBy(String closedBy) { this.closedBy = closedBy; }

    public String getClosureCode() { return closureCode; }

    public void setClosureCode(String closureCode) { this.closureCode = closureCode; }

    public String getAffectedItem() { return affectedItem; }

    public void setAffectedItem(String affectedItem) { this.affectedItem = affectedItem; }

    public String getAffectedItemSysId() { return affectedItemSysId; }

    public String getAffectedItemDisplayId() { return affectedItemDisplayId; }

    public void setAffectedItemDisplayId(String affectedItemDisplayId) { this.affectedItemDisplayId = affectedItemDisplayId; }

    public void setAffectedItemSysId(String affectedItemSysId) { this.affectedItemSysId = affectedItemSysId; }

    public String getIncidentDescription() { return incidentDescription; }

    public void setIncidentDescription(String incidentDescription) { this.incidentDescription = incidentDescription; }

    public String getIncidentSysID() { return incidentSysID; }

    public void setIncidentSysID(String incidentSysID) { this.incidentSysID = incidentSysID; }

    public String getAffectedBusinessServiceItem() { return affectedBusinessServiceItem; }

    public void setAffectedBusinessServiceItem(String affectedBusinessServiceItem) { this.affectedBusinessServiceItem = affectedBusinessServiceItem; }

    public String getAffectedBusinessServiceItemSysId() { return affectedBusinessServiceItemSysId; }

    public void setAffectedBusinessServiceItemSysId(String affectedBusinessServiceItemSysId) { this.affectedBusinessServiceItemSysId = affectedBusinessServiceItemSysId; }

    public String getAffectedBusinessServiceItemDisplayId() { return affectedBusinessServiceItemDisplayId; }

    public void setAffectedBusinessServiceItemDisplayId(String affectedBusinessServiceItemDisplayId) { this.affectedBusinessServiceItemDisplayId = affectedBusinessServiceItemDisplayId; }

    @Override
    public boolean equals(Object compareTo){
        boolean doesEqual = true;

        if(compareTo == null || !compareTo.getClass().isAssignableFrom(Incident.class)){
            doesEqual = false;
        }else {
            Incident newIncident = (Incident) compareTo;

            if(!newIncident.toString().equals(toString())){
                doesEqual = false;
            }
        }

        return doesEqual;
    }

    /**
     *  Returns human readable string of the Incident Object.
     *  * equals(Object object) depends on this method. Changing this method could alter the return of the equals method.
     * @return object to string
     */
    @Override
    public String toString() {

        StringBuffer buf = new StringBuffer(210);
        buf.append("incidentID: ")
                .append(incidentID);

        return buf.toString();
    }

    @Override
    public int hashCode(){
        return Objects.hash(incidentID);
    }

}
