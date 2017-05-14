package com.capitalone.dashboard.model;


public class CollectionError {

    private String errorCode;
    private String errorMessage;
    private long timestamp;

    public static final String UNKNOWN_HOST = "Unreachable";

    public CollectionError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
    }

    public String getErrorCode() {
        return errorCode;
    }


    public String getErrorMessage() {
        return errorMessage;
    }


    public long getTimestamp() {
        return timestamp;
    }

}
