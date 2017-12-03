package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection="incident")
public class Incident extends BaseModel {

    private ObjectId collectorItemId;
    private String timestamp;
    private String incidentItem;
    private String incidentID;
    private String category;
    private String openTime;
    private String openedBy;
    private String severity;
    private String updatedTime;
    private String primaryAssignmentGroup;
    private String status;
    private String closedTime;
    private String closedBy;
    private String closureCode;
    private String affectedItem;
    private String incidentDescription;
    private boolean validIncidentItem;

    public ObjectId getCollectorItemId() { return collectorItemId; }

    public void setCollectorItemId(ObjectId collectorItemId) { this.collectorItemId = collectorItemId; }

    public String getTimestamp() { return timestamp; }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getIncidentItem() { return incidentItem; }

    public void setIncidentItem(String incidentItem) { this.incidentItem = incidentItem; }

    public String getIncidentID() { return incidentID; }

    public void setIncidentID(String incidentID) { this.incidentID = incidentID; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public String getOpenTime() { return openTime; }

    public void setOpenTime(String openTime) { this.openTime = openTime; }

    public String getOpenedBy() { return openedBy; }

    public void setOpenedBy(String openedBy) { this.openedBy = openedBy; }

    public String getSeverity() { return severity; }

    public void setSeverity(String severity) { this.severity = severity; }

    public String getUpdatedTime() { return updatedTime; }

    public void setUpdatedTime(String updatedTime) { this.updatedTime = updatedTime; }

    public String getPrimaryAssignmentGroup() { return primaryAssignmentGroup; }

    public void setPrimaryAssignmentGroup(String primaryAssignmentGroup) { this.primaryAssignmentGroup = primaryAssignmentGroup; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getClosedTime() { return closedTime; }

    public void setClosedTime(String closedTime) { this.closedTime = closedTime; }

    public String getClosedBy() { return closedBy; }

    public void setClosedBy(String closedBy) { this.closedBy = closedBy; }

    public String getClosureCode() { return closureCode; }

    public void setClosureCode(String closureCode) { this.closureCode = closureCode; }

    public String getAffectedItem() { return affectedItem; }

    public void setAffectedItem(String affectedItem) { this.affectedItem = affectedItem; }

    public String getIncidentDescription() { return incidentDescription; }

    public void setIncidentDescription(String incidentDescription) { this.incidentDescription = incidentDescription; }

    public void setValidIncidentItem(boolean validIncidentItem) { this.validIncidentItem = validIncidentItem; }

    public void setValidIncidentItem(String validIncidentItem) { this.validIncidentItem = Boolean.parseBoolean(validIncidentItem); }

    public boolean isValidIncidentItem() { return validIncidentItem; }

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
