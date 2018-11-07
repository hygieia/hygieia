package com.capitalone.dashboard.client;

public class RestUserInfo {
    private String userId;
    private String passCode;

    public RestUserInfo(String userId, String passCode) {
        this.userId = userId;
        this.passCode = passCode;
    }

    public String getFormattedString() {
        return userId.trim() + ":" + passCode.trim();
    }
}

