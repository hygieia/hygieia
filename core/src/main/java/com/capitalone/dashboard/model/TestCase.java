package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An individual test case in a {@link TestSuite}.
 */
public class TestCase {
    /**
     * Identifies this test case in the source system
     */
    private String id;

    /**
     * Description of the test case that might make sense to a human
     */
    private String description;

    private long duration;

    private int totalTestStepCount;
    /**
     * Count of test cases that failed
     */
    private int failedTestStepCount;

    /**
     * Count of test cases that generated an error
     */
    private int successTestStepCount;

    /**
     * Count of test cases that were skipped
     */
    private int skippedTestStepCount;

    private int unknownStatusTestStepCount;
    /**
     * Status of the test case
     */
    private TestCaseStatus status = TestCaseStatus.Unknown;

    private Collection<TestCaseStep> testSteps = new ArrayList<>();


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

    public int getTotalTestStepCount() {
        return totalTestStepCount;
    }

    public void setTotalTestStepCount(int totalTestStepCount) {
        this.totalTestStepCount = totalTestStepCount;
    }

    public int getFailedTestStepCount() {
        return failedTestStepCount;
    }

    public void setFailedTestStepCount(int failedTestStepCount) {
        this.failedTestStepCount = failedTestStepCount;
    }

    public int getSuccessTestStepCount() {
        return successTestStepCount;
    }

    public void setSuccessTestStepCount(int successTestStepCount) {
        this.successTestStepCount = successTestStepCount;
    }

    public int getSkippedTestStepCount() {
        return skippedTestStepCount;
    }

    public void setSkippedTestStepCount(int skippedTestStepCount) {
        this.skippedTestStepCount = skippedTestStepCount;
    }

    public int getUnknownStatusCount() {
        return unknownStatusTestStepCount;
    }

    public void setUnknownStatusCount(int unknownStatusCount) {
        this.unknownStatusTestStepCount = unknownStatusCount;
    }

    public TestCaseStatus getStatus() {
        return status;
    }

    public void setStatus(TestCaseStatus status) {
        this.status = status;
    }

    public void setTestSteps(Collection<TestCaseStep> testSteps) {
        this.testSteps = testSteps;
    }

    public Collection<TestCaseStep> getTestSteps() {
        return testSteps;
    }

}
