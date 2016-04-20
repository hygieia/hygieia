package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;

/**
 * A collection of {@link TestCase}s of a particular {@link TestSuiteType}.
 */
@Data
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

}
