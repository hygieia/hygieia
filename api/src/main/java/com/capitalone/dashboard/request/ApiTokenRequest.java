package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

public class ApiTokenRequest {

    @NotNull
    private String apiUser;

    @NotNull
    private Long expirationDt;

    public ApiTokenRequest() {

    }

    public String getApiUser() {
        return apiUser;
    }

    public void setApiUser(String apiUser) {
        this.apiUser = apiUser;
    }

    public Long getExpirationDt() {
        return expirationDt;
    }

    public void setExpirationDt(Long expirationDt) {
        this.expirationDt = expirationDt;
    }
}
