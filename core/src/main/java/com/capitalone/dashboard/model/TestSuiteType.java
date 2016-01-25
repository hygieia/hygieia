package com.capitalone.dashboard.model;

/**
 * Denotes the type of tests in a given {@link TestSuite}.
 */
public enum TestSuiteType {
    Unit, Functional, Regression, Performance, Integration, Security;

    public static TestSuiteType fromString(String value) {
        for (TestSuiteType type : values()) {
            if (type.toString().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid TestSuiteType.");
    }
}
