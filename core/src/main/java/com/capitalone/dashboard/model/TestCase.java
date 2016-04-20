package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;

/**
 * An individual test case in a {@link TestSuite}.
 */
@Data
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

}
