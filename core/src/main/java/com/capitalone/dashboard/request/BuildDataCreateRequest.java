package com.capitalone.dashboard.request;

import com.capitalone.dashboard.model.RepoBranch;
import com.capitalone.dashboard.model.SCM;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


public class BuildDataCreateRequest {

    @NotNull
    private String number;
    @NotNull
    private String buildUrl;
    @NotNull
    private String jobName;
    @NotNull
    private String buildStatus;
    @NotNull
    private long startTime;
    @NotNull
    private String jobUrl;
    @NotNull
    private String instanceUrl;

    private String niceName;

    private long endTime;
    private long duration;
    private String startedBy;
    private String log;
    private List<RepoBranch> codeRepos = new ArrayList<>();
    private List<SCM> sourceChangeSet = new ArrayList<>();

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBuildUrl() {
        return buildUrl;
    }

    public void setBuildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getBuildStatus() {
        return buildStatus;
    }

    public void setBuildStatus(String buildStatus) {
        this.buildStatus = buildStatus;
    }

    public String getStartedBy() {
        return startedBy;
    }

    public void setStartedBy(String startedBy) {
        this.startedBy = startedBy;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public List<SCM> getSourceChangeSet() {
        return sourceChangeSet;
    }

    public void setSourceChangeSet(List<SCM> sourceChangeSet) {
        this.sourceChangeSet = sourceChangeSet;
    }


    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public String getInstanceUrl() {
        return instanceUrl;
    }

    public void setInstanceUrl(String instanceUrl) {
        this.instanceUrl = instanceUrl;
    }

    public String getNiceName() {
        return niceName;
    }

    public void setNiceName(String niceName) {
        this.niceName = niceName;
    }

    public void setCodeRepos(List<RepoBranch> codeRepos) {
        this.codeRepos = codeRepos;
    }

    public List<RepoBranch> getCodeRepos() {
        return codeRepos;
    }
}
