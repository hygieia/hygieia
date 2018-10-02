package com.capitalone.dashboard.model;

import com.capitalone.dashboard.response.DashboardReviewResponse;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "audit_results")
public class AuditResult extends BaseModel {

    private ObjectId dashboardId;
    private String dashboardTitle;
    private DashboardReviewResponse dashboardReview;
    private long timestamp;

    public AuditResult(ObjectId dashboardId, String dashboardTitle, DashboardReviewResponse dashboardReview,
                       long timestamp) {
        this.dashboardId = dashboardId;
        this.dashboardTitle = dashboardTitle;
        this.dashboardReview = dashboardReview;
        this.timestamp = timestamp;
    }

    public ObjectId getDashboardId() {
        return dashboardId;
    }

    public DashboardReviewResponse getDashboardReview() {
        return dashboardReview;
    }

    public String getDashboardTitle() {
        return dashboardTitle;
    }

    public long getTimestamp() {
        return timestamp;
    }
}