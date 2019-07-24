package com.capitalone.dashboard.gitlab.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitlabRequestChanges {

    private String id;
    private String iid;
    private String title;
    private String state;

    @JsonProperty("merge_commit_sha")
    private String mergeCommitSha;
    @JsonProperty("changes_count")
    private String changesCount;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the iid
     */
    public String getIid() {
        return iid;
    }

    /**
     * @param iid
     *            the iid to set
     */
    public void setIid(String iid) {
        this.iid = iid;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the mergeCommitSha
     */
    public String getMergeCommitSha() {
        return mergeCommitSha;
    }

    /**
     * @param mergeCommitSha
     *            the mergeCommitSha to set
     */
    public void setMergeCommitSha(String mergeCommitSha) {
        this.mergeCommitSha = mergeCommitSha;
    }

    /**
     * @return the changesCount
     */
    public String getChangesCount() {
        return changesCount;
    }

    /**
     * @param changesCount
     *            the changesCount to set
     */
    public void setChangesCount(String changesCount) {
        this.changesCount = changesCount;
    }

}
