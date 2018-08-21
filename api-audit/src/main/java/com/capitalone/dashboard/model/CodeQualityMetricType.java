package com.capitalone.dashboard.model;

public enum CodeQualityMetricType {
	BLOCKER_VIOLATIONS("blocker_violations"),
	CRITICAL_VIOLATIONS("critical_violations"),
	MAJOR_VIOLATIONS("major_violations"),
	VIOLATIONS_DENSITY("violations_density"),
	VIOLATIONS("violations"),
	UNIT_TEST("test_success_density"),
	TEST("tests"),
	TEST_FAILURE("test_failures"),
	TEST_ERROR("test_errors"),
	NEW_COVERAGE("new_coverage"),
	COVERAGE("coverage"),
	LINE_COVERAGE("line_coverage");

	private String type;

	CodeQualityMetricType(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return this.type;
	}
}
