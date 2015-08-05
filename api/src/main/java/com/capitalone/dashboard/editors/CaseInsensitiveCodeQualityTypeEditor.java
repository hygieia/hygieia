package com.capitalone.dashboard.editors;

import com.capitalone.dashboard.model.CodeQualityType;

import java.beans.PropertyEditorSupport;

/**
 * Property editor that translates text into a CodeQualityType in a case insensitive manner.
 */
public class CaseInsensitiveCodeQualityTypeEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(CodeQualityType.fromString(text));
    }
}
