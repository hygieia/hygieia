package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Document(collection="gitrequests")
public class GitRequest  {

    // fields added from SCM class that we are not extending anymore
    private String scmUrl;
    private String scmBranch;
    private String scmRevisionNumber;
    //squash merge commit may be different from pr commit
    private String scmMergeEventRevisionNumber;
    private String scmCommitLog;
    private long scmCommitTimestamp;
    private String scmAuthor;
    private long numberOfChanges;
    // fields added above are from SCM class that we are not extending anymore

    @Id
    private ObjectId id;
    private String orgName;
    private String repoName;
    private String sourceRepo;
    private String sourceBranch;
    private String targetRepo;
    private String targetBranch;
    private String number;
    @Indexed
    private ObjectId collectorItemId;
    private long updatedAt;
    private long createdAt;
    private long closedAt;
    private String state;
    private long mergedAt;

    private long timestamp;
    private long resolutiontime;
    private String userId = null;
    private String commentsUrl;
    private String reviewCommentsUrl;
    private List<Comment> comments;
    private List<Review> reviews;
    private List<CommitStatus> commitStatuses;
    private String headSha;
    private String baseSha;
    private String requestType;
    private List<Commit> commits;


    public long getNumberOfChanges() {
        return numberOfChanges;
    }

    public void setNumberOfChanges(long numberOfChanges) {
        this.numberOfChanges = numberOfChanges;
    }

    public String getScmAuthor() {
        return scmAuthor;
    }

    public void setScmAuthor(String scmAuthor) {
        this.scmAuthor = scmAuthor;
    }

    public long getScmCommitTimestamp() {
        return scmCommitTimestamp;
    }

    public void setScmCommitTimestamp(long scmCommitTimestamp) {
        this.scmCommitTimestamp = scmCommitTimestamp;
    }

    public String getScmBranch() {
        return scmBranch;
    }

    public void setScmBranch(String scmBranch) {
        this.scmBranch = scmBranch;
    }

    public String getScmCommitLog() {
        return scmCommitLog;
    }

    public void setScmCommitLog(String scmCommitLog) {
        this.scmCommitLog = scmCommitLog;
    }

    public String getScmUrl() {
        return scmUrl;
    }

    public void setScmUrl(String scmUrl) {
        this.scmUrl = scmUrl;
    }

    public String getScmRevisionNumber() {
        return scmRevisionNumber;
    }

    public void setScmRevisionNumber(String scmRevisionNumber) {
        this.scmRevisionNumber = scmRevisionNumber;
    }

    public String getScmMergeEventRevisionNumber() {
        return scmMergeEventRevisionNumber;
    }

    public void setScmMergeEventRevisionNumber(String scmMergeEventRevisionNumber) {
        this.scmMergeEventRevisionNumber = scmMergeEventRevisionNumber;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }


    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getSourceRepo() {
        return sourceRepo;
    }

    public void setSourceRepo(String sourceRepo) {
        this.sourceRepo = sourceRepo;
    }

    public String getSourceBranch() {
        return sourceBranch;
    }

    public void setSourceBranch(String sourceBranch) {
        this.sourceBranch = sourceBranch;
    }

    public String getTargetRepo() {
        return targetRepo;
    }

    public void setTargetRepo(String targetRepo) {
        this.targetRepo = targetRepo;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    public void setTargetBranch(String targetBranch) {
        this.targetBranch = targetBranch;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(long closedAt) {
        this.closedAt = closedAt;
    }

    public long getMergedAt() {
        return mergedAt;
    }

    public void setMergedAt(long mergedAt) {
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCommentsUrl() {
        return commentsUrl;
    }

    public void setCommentsUrl(String commentsUrl) {
        this.commentsUrl = commentsUrl;
    }

    public String getReviewCommentsUrl() {
        return reviewCommentsUrl;
    }

    public void setReviewCommentsUrl(String reviewCommentsUrl) {
        this.reviewCommentsUrl = reviewCommentsUrl;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<CommitStatus> getCommitStatuses() {
        return commitStatuses;
    }

    public void setCommitStatuses(List<CommitStatus> commitStatuses) {
        this.commitStatuses = commitStatuses;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits;
    }

    public String getHeadSha() {
        return headSha;
    }

    public void setHeadSha(String headSha) {
        this.headSha = headSha;
    }

    public String getBaseSha() {
        return baseSha;
    }

    public void setBaseSha(String baseSha) {
        this.baseSha = baseSha;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
