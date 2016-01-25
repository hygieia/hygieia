package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.CodeQualityMetric;
import com.capitalone.dashboard.model.CodeQualityType;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CodeQualityCreateRequest {
    private String hygieiaId;
    @NotNull
    private long timestamp;
    @NotNull
    private String projectName;
    @NotNull
    private String projectId;
    @NotNull
    private String projectUrl;
    @NotNull
    private String serverUrl;
    @NotNull
    private CodeQualityType type;
    @NotNull
    private String projectVersion;

    private String niceName;

    private List<CodeQualityMetric> metrics = new ArrayList<>();



    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public CodeQualityType getType() {
        return type;
    }

    public void setType(CodeQualityType type) {
        this.type = type;
    }

    public String getHygieiaId() {
        return hygieiaId;
    }

    public void setHygieiaId(String hygieiaId) {
        this.hygieiaId = hygieiaId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public List<CodeQualityMetric> getMetrics() {
        return metrics;
    }

    public String getNiceName() {
        return niceName;
    }

    public void setNiceName(String niceName) {
        this.niceName = niceName;
    }
}
