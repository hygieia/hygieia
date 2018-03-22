package com.capitalone.dashboard.model;

public enum ScoreDisplayType {
    HEADER,
    WIDGET;

    public static ScoreDisplayType fromString(String value){
        for(ScoreDisplayType scoreDisplay : values()){
            if(scoreDisplay.toString().equalsIgnoreCase(value)){
                return scoreDisplay;
            }
        }
        return ScoreDisplayType.HEADER;
    }
}
