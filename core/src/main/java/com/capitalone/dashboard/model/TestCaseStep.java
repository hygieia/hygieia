package com.capitalone.dashboard.model;


public class TestCaseStep {

    /**
     * Identifies this test case step in the source system
     */
    private String id;

    /**
     * Description of the test case step that might make sense to a human
     */
    private String description;

    /**
     * Test case duration in milliseconds
     */
    private long duration;

    /**
     * Status of the test case step
     */
    private TestCaseStatus status;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public TestCaseStatus getStatus() {
        return status;
    }

    public void setStatus(TestCaseStatus status) {
        this.status = status;
    }

}
