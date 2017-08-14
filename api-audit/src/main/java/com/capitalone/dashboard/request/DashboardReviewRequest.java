package com.capitalone.dashboard.request;

public class DashboardReviewRequest extends AuditReviewRequest {
    private String title;
    private String type;

    //asv
    private String busServ;

    //bap
    private String busApp;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBusServ() {
        return busServ;
    }

    public void setBusServ(String busServ) {
        this.busServ = busServ;
    }

    public String getBusApp() {
        return busApp;
    }

    public void setBusApp(String busApp) {
        this.busApp = busApp;
    }
}
