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
@Document(collection="issues")
public class Issue extends SCM {
    @Id
    private ObjectId id;

    //Thus could be user id in GIT
    private String orgName;
    private String repoName;
    private String number;
    //Could be Different from name in GIT
    private String developerName = null;
    private String userId = null;
    private String departmentId = null;
    private String departmentName = null;
    private String manager = null;
    private String jobLevel = null;

    //Typically metrics are captured at two levels of leadership
    // You could use it for project name and org name if you want to capture metrics that way
    private String levelOneMgr = null;
    private String levelTwoMgr = null;
    private ObjectId collectorItemId;

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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    private String createdAt;
    private String closedAt;
    private long timestamp;
    public long getResolutiontime() {
        return resolutiontime;
    }

    public void setResolutiontime(long resolutiontime) {
        this.resolutiontime = resolutiontime;
    }

    private long resolutiontime;
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

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
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

    public ObjectId getCollectorItemId() {
        return collectorItemId;
    }

    public void setCollectorItemId(ObjectId collectorItemId) {
        this.collectorItemId = collectorItemId;
    }

}
