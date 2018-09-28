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

    public DashboardReviewResponse getDashboardReviewResponse() {
        return dashboardReviewResponse;
    }

    public String getDashboardTitle() {
        return dashboardReviewResponse.getDashboardTitle();
    }

}