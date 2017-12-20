package com.capitalone.dashboard.model;

import java.util.ArrayList;
import java.util.Collection;

public class PerfTest {

    private String testName;

    private long timeStamp;

    private String runId;

    private long startTime;

    private long endTime;

    private String resultStatus;

    Collection<PerfIndicators> perfIndicators = new ArrayList<>();


    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
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

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public Collection<PerfIndicators> getPerfIndicators() {
        return perfIndicators;
    }

    public void setPerfIndicators(Collection<PerfIndicators> perfIndicators) {
        this.perfIndicators = perfIndicators;
    }


    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}
