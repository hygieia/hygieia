package com.capitalone.dashboard.editors;

import com.capitalone.dashboard.model.ServiceStatus;

import java.beans.PropertyEditorSupport;

/**
 * Property editor that translates text into a BuildStatus in a case insensitive manner.
 */
public class CaseInsensitiveServiceStatusEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(ServiceStatus.fromString(text));
    }
}
