package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A collection of {@link TestCase}s of a particular {@link TestSuiteType}.
 */
public class TestSuite {

    private String id;
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

    private int totalTestCaseCount;
    /**
     * Count of test cases that failed
     */
    private int failedTestCaseCount;

    /**
     * Count of test cases that generated an error
     */
    private int successTestCaseCount;

    /**
     * Count of test cases that were skipped
     */
    private int skippedTestCaseCount;


    private int unknownStatusCount;

    private TestCaseStatus status;

    /**
     * Collection of {@link TestCase}s associated with this suite
     */
    private Collection<TestCase> testCases = new ArrayList<>();


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

    public TestCaseStatus getStatus() {
        return status;
    }

    public void setStatus(TestCaseStatus status) {
        this.status = status;
    }

    public int getFailedTestCaseCount() {
        return failedTestCaseCount;
    }

    public int getTotalTestCaseCount() {
        return totalTestCaseCount;
    }

    public void setTotalTestCaseCount(int totalTestCaseCount) {
        this.totalTestCaseCount = totalTestCaseCount;
    }

    public void setFailedTestCaseCount(int failedTestCaseCount) {
        this.failedTestCaseCount = failedTestCaseCount;
    }

    public int getSuccessTestCaseCount() {
        return successTestCaseCount;
    }

    public void setSuccessTestCaseCount(int successTestCaseCount) {
        this.successTestCaseCount = successTestCaseCount;
    }

    public int getSkippedTestCaseCount() {
        return skippedTestCaseCount;
    }

    public void setSkippedTestCaseCount(int skippedTestCaseCount) {
        this.skippedTestCaseCount = skippedTestCaseCount;
    }

    public int getUnknownStatusCount() {
        return unknownStatusCount;
    }

    public void setUnknownStatusCount(int unknownStatusCount) {
        this.unknownStatusCount = unknownStatusCount;
    }

    public Collection<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(Collection<TestCase> testCases) {
        this.testCases = testCases;
    }

}
