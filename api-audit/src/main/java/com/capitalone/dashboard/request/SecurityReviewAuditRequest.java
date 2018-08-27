package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.CodeQualityType;
import javax.validation.constraints.NotNull;

public class SecurityReviewAuditRequest extends AuditReviewRequest {

    @NotNull
    private String appASV;
    @NotNull
    private String component;
    private CodeQualityType type;

    public CodeQualityType getType() {
        return type;
    }

    public void setType(CodeQualityType type) {
        this.type = type;
    }

    public String getAppASV() {
        return appASV;
    }

    public void setAppASV(String appASV) {
        this.appASV = appASV;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }
}
