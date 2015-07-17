package com.capitalone.dashboard.editors;

import com.capitalone.dashboard.model.CollectorType;

import java.beans.PropertyEditorSupport;

/**
 * Property editor that translates text into a CollectorType in a case insensitive manner.
 */
public class CaseInsensitiveCollectorTypeEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(CollectorType.fromString(text));
    }
}
