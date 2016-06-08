package com.capitalone.dashboard.model;

public class TestCaseConditionResult {
    private TestCaseStatus status;
    private long duration;

    public TestCaseStatus getStatus() {
        return status;
    }

    public void setStatus(TestCaseStatus status) {
        this.status = status;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
