package com.capitalone.dashboard.api.domain;

import java.net.URI;

/**
 * This class will get the details from a Test Set
 */
public class TestSet extends VersionableIssue<TestSet> {

    public TestSet(URI self, String key, Long id) {
        super(self, key, id);
    }

    @Override
    public TestSet cloneTest() throws CloneNotSupportedException {
        //TODO: Implement TestSet later on
        return null;
    }
}
