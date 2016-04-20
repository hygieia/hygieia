package com.capitalone.dashboard.model;

import lombok.Data;

@Data
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

}
