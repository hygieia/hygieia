package com.capitalone.dashboard.model;

public class TestCaseCondition {
    private String condition;
    private TestCaseConditionResult result;


    private class TestCaseConditionResult {
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

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public TestCaseConditionResult getResult() {
        return result;
    }

    public void setResult(TestCaseConditionResult result) {
        this.result = result;
    }
}
