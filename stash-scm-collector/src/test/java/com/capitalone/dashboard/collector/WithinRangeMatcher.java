package com.capitalone.dashboard.collector;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class WithinRangeMatcher extends TypeSafeMatcher<Long> {
    private final Long lhs;
    private final Long rhs;

    private WithinRangeMatcher(final Long lhs, final Long rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    protected boolean matchesSafely(final Long actual) {
        return lhs <= actual && rhs >= actual;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("value should be within range ").appendValue(lhs).appendText(" and ").appendValue(rhs);
    }

    public static WithinRangeMatcher withinRange(final Long lhs, final Long rhs) {
        return new WithinRangeMatcher(lhs, rhs);
    }
}
