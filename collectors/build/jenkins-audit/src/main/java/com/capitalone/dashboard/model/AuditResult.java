package com.capitalone.dashboard.model;

import com.capitalone.dashboard.response.DashboardReviewResponse;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "audit_results")
public class AuditResult extends BaseModel {

    private ObjectId dashboardId;
    private DashboardReviewResponse dashboardReviewResponse;
    private long timestamp;


    public AuditResult(ObjectId dashboardId, DashboardReviewResponse dashboardReviewResponse, long timestamp) {
        this.dashboardId = dashboardId;
        this.dashboardReviewResponse = dashboardReviewResponse;
        this.timestamp = timestamp;
    }

    public ObjectId getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(ObjectId dashboardId) {
        this.dashboardId = dashboardId;
    }

    public DashboardReviewResponse getDashboardReviewResponse() {
        return dashboardReviewResponse;
    }

    public void setDashboardReviewResponse(DashboardReviewResponse dashboardReviewResponse) {
        this.dashboardReviewResponse = dashboardReviewResponse;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDashboardTitle() {
        return dashboardReviewResponse.getDashboardTitle();
    }

    public String getBusinessService() {
        return dashboardReviewResponse.getBusinessService();
    }

    public String getBusinessApplication() {
        return dashboardReviewResponse.getBusinessApplication();
    }

}