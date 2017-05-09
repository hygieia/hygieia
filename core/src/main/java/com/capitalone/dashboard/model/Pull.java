package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A specific commit in a version control repository.
 *
 * Possible collectors:
 *  Subversion (in scope)
 *  Git (in scope)
 *  GitHub
 *  TFS
 *  BitBucket
 *  Unfuddle
 *
 */
@Document(collection="pulls")
public class Pull extends SCM {
    @Id
    private ObjectId id;
    private String orgName;
    private String repoName;
    private String number;
    private ObjectId collectorItemId;
    private String createdAt;
    private String closedAt;
    private String mergedAt;
    private long timestamp;
    private long resolutiontime;
    private String userId = null;

    //These fields are for another collector to fill later - the ones
    //that have access to people data. Let that collector fill only
    //if that is set to TBD
    //These are used to track contributions by
    //developers and their LOB/leadership etc. This helps track where your PRs
    //are coming from. There wil
    //Typically metrics are captured at two levels of leadership
    // You could use it for project name and org name if you want to capture metrics that way
    private String developerName = "TBD";
    private String departmentId = "TBD";
    private String departmentName = "TBD";
    private String manager = "TBD";
    private String jobLevel = "TBD";
    private String levelOneMgr = "TBD";
    private String levelTwoMgr = "TBD";


    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(String closedAt) {
        this.closedAt = closedAt;
    }

    public String getMergedAt() {
        return mergedAt;
    }

    public void setMergedAt(String mergedAt) {
        this.mergedAt = mergedAt;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getResolutiontime() {
        return resolutiontime;
    }

    public void setResolutiontime(long resolutiontime) {
        this.resolutiontime = resolutiontime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getJobLevel() {
        return jobLevel;
    }

    public void setJobLevel(String jobLevel) {
        this.jobLevel = jobLevel;
    }

    public String getLevelOneMgr() {
        return levelOneMgr;
    }

    public void setLevelOneMgr(String levelOneMgr) {
        this.levelOneMgr = levelOneMgr;
    }

    public String getLevelTwoMgr() {
        return levelTwoMgr;
    }

    public void setLevelTwoMgr(String levelTwoMgr) {
        this.levelTwoMgr = levelTwoMgr;
    }

}