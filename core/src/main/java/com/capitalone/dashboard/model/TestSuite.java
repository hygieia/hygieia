package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A collection of {@link TestCase}s of a particular {@link TestSuiteType}.
 */
public class TestSuite {

    /**
     * Description of the test suite that might make sense to a human
     */
    private String description;

    /**
     * Type of test
     */
    private TestSuiteType type;

    /**
     * Start test suite execution time {@link java.util.Date#getTime()}
     */
    private long startTime;

    /**
     * End test suite execution time {@link java.util.Date#getTime()}
     */
    private long endTime;

    /**
     * Test suite duration in milliseconds
     */
    private long duration;

    /**
     * Count of test cases that failed
     */
    private int failureCount;

    /**
     * Count of test cases that generated an error
     */
    private int errorCount;

    /**
     * Count of test cases that were skipped
     */
    private int skippedCount;

    /**
     * The total number of test cases
     */
    private int totalCount;

    /**
     * Collection of {@link TestCase}s associated with this suite
     */
    private Collection<TestCase> testCases = new ArrayList<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TestSuiteType getType() {
        return type;
    }

    public void setType(TestSuiteType type) {
        this.type = type;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(int skippedCount) {
        this.skippedCount = skippedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public Collection<TestCase> getTestCases() {
        return testCases;
    }
}
