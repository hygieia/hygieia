package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


public class CloudSubnetListRefreshRequest {

    @NotNull
    private String accountNumber;
    @NotNull
    private List<String> subnetIds;
    private Date refreshDate;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public List<String> getSubnetIds() {
        return subnetIds;
    }

    public void setSubnetIds(List<String> subnetIds) {
        this.subnetIds = subnetIds;
    }

    public Date getRefreshDate() {
        return refreshDate;
    }

    public void setRefreshDate(Date refreshDate) {
        this.refreshDate = refreshDate;
    }
}
