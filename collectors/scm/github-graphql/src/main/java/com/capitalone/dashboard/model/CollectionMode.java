package com.capitalone.dashboard.model;


public enum CollectionMode {
    FirstTimeAll,
    FirstTimeCommitOnly,
    FirstTimeCommitAndPull,
    FirstTimeCommitAndIssue,
    CommitOnly,
    PullOnly,
    IssueOnly,
    CommitAndIssue,
    CommitAndPull,
    PullAndIssue,
    All,
    None
}