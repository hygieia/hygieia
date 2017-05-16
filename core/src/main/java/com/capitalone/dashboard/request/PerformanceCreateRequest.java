package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.PerformanceMetric;
import com.capitalone.dashboard.model.PerformanceType;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class PerformanceCreateRequest {
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
    private PerformanceType type;
    @NotNull
    private String projectVersion;
    @NotNull
    private String collectorName;

    private String niceName;

    private List<PerformanceMetric> metrics = new ArrayList<>();



    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public PerformanceType getType() {
        return type;
    }

    public void setType(PerformanceType type) {
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

    public List<PerformanceMetric> getMetrics() {
        return metrics;
    }

    public String getNiceName() {
        return niceName;
    }

    public void setNiceName(String niceName) {
        this.niceName = niceName;
    }

    public String getCollectorName() {
        return collectorName;
    }

    public void setCollectorName(String collectorName) {
        this.collectorName = collectorName;
    }
}
