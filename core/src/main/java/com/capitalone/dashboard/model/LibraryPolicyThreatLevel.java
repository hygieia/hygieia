package com.capitalone.dashboard.model;

/**
 * Enumerates the possible statuses.
 */
public enum LibraryPolicyThreatLevel {
    High, Medium, Low, None;

    public static LibraryPolicyThreatLevel fromString(String value){
        for(LibraryPolicyThreatLevel threatLevel : values()){
            if(threatLevel.toString().equalsIgnoreCase(value)){
                return threatLevel;
            }
        }
        throw new IllegalArgumentException(value+" is not a valid LibraryPolicyThreatLevel");
    }

    public static LibraryPolicyThreatLevel fromDouble(double value){

        if ((value <=10.0) && (value >= 7.0)) return LibraryPolicyThreatLevel.High;
        if ((value < 7.0) && (value >= 4.0)) return LibraryPolicyThreatLevel.Medium;
        if ((value < 4.0) && (value > 0.0)) return LibraryPolicyThreatLevel.Low;
        if (value == 0.0) return LibraryPolicyThreatLevel.None;

        throw new IllegalArgumentException(value+" is not a valid LibraryPolicyThreatLevel");
    }

    public static LibraryPolicyThreatLevel fromInt(int value){
        double dv = (double) value;
        return fromDouble(dv);
    }

}
