package com.capitalone.dashboard.editors;

import com.capitalone.dashboard.model.BuildStatus;

import java.beans.PropertyEditorSupport;

/**
 * Property editor that translates text into a BuildStatus in a case insensitive manner.
 */
public class CaseInsensitiveBuildStatusEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(BuildStatus.fromString(text));
    }
}
