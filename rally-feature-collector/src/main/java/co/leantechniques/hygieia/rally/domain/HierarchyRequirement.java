package co.leantechniques.hygieia.rally.domain;

import com.capitalone.dashboard.model.*;
import com.google.gson.annotations.SerializedName;

public class HierarchyRequirement extends RallyObject {

    @SerializedName("FormattedID")
    String formattedID;
    @SerializedName("Name")
    String name;
    @SerializedName("ScheduleState")
    String scheduleState;

    @SerializedName("Blocked")
    boolean blocked;

    @SerializedName("PlanEstimate")
    String planEstimate;

    @SerializedName("Iteration")
    Iteration iteration;

    @SerializedName("Release")
    Release release;
    @SerializedName("Project")
    Project project;
    @SerializedName("Feature")
    Feature feature;

    public String getFormattedID() {
        return formattedID;
    }

    public void setFormattedID(String formattedID) {
        this.formattedID = formattedID;
    }

    public String getScheduleState() {
        return scheduleState;
    }

    public void setScheduleState(String scheduleState) {
        this.scheduleState = scheduleState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanEstimate() {
        return planEstimate;
    }

    public void setPlanEstimate(String planEstimate) {
        this.planEstimate = planEstimate;
    }


}
