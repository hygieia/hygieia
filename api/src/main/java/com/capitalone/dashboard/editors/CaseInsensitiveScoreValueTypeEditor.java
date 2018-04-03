package com.capitalone.dashboard.editors;

import com.capitalone.dashboard.model.score.ScoreValueType;

import java.beans.PropertyEditorSupport;

/**
 * Property editor that translates text into a ScoreValueType in a case insensitive manner.
 */
public class CaseInsensitiveScoreValueTypeEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(ScoreValueType.fromString(text));
    }
}
