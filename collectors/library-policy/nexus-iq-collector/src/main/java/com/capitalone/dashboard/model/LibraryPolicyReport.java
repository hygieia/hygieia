package com.capitalone.dashboard.model;

public class LibraryPolicyReport {
    private String stage;
    private long evaluationDate;
    private String reportDataUrl;
    private String reportUIUrl;

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public long getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(long evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public String getReportDataUrl() {
        return reportDataUrl;
    }

    public void setReportDataUrl(String reportDataUrl) {
        this.reportDataUrl = reportDataUrl;
    }

    public String getReportUIUrl() {
        return reportUIUrl;
    }

    public void setReportUIUrl(String reportUIUrl) {
        this.reportUIUrl = reportUIUrl;
    }
}