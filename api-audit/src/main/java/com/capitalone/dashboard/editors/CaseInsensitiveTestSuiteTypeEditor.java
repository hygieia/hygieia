package com.capitalone.dashboard.editors;

import com.capitalone.dashboard.model.TestSuiteType;

import java.beans.PropertyEditorSupport;

/**
 * Property editor that translates text into a TestSuiteType in a case insensitive manner.
 */
public class CaseInsensitiveTestSuiteTypeEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(TestSuiteType.fromString(text));
    }
}
