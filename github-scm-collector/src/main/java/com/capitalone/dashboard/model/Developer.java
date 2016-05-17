package com.capitalone.dashboard.model;

/**
 * Created by ltz038 on 5/7/16.
 */

//This is not a Collecor Item - this is used when collecting GIT data
public class Developer {
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

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    private String userId = null;
    private String departmentId = null;
    private String departmentname = null;
    private String name = null;
    private String manager = null;
    private String jobLevel = null;
    private String levelOneMgr = null;
    private String levelTwoMgr = null;
}
