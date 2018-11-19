package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;

public class ServiceAccountRequest {

    @NotNull
    private String serviceAccount;

    @NotNull
    private String fileNames;

    public ServiceAccountRequest() {

    }

    public String getServiceAccount() {
        return serviceAccount;
    }

    public void setServiceAccount(String serviceAccount) {
        this.serviceAccount = serviceAccount;
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }
}
