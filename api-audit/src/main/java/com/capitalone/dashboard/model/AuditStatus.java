package com.capitalone.dashboard.model;

public enum AuditStatus {
    //commit author v/s who merged the pr
    COMMITAUTHOR_NE_MERGECOMMITER,
    COMMITAUTHOR_EQ_MERGECOMMITER,

    //peer review of a pull request
    PULLREQ_REVIEWED_BY_PEER,
    PULLREQ_NOT_PEER_REVIEWED,

    //no pull requests for queried date range
    NO_PULL_REQ_FOR_DATE_RANGE,

    // Git repo not configured
    REPO_NOT_CONFIGURED,

    //direct commits to master
    DIRECT_COMMITS_TO_BASE,
    DIRECT_COMMITS_TO_BASE_FIRST_COMMIT,

    //type of git workflow
    GIT_FORK_STRATEGY,
    GIT_BRANCH_STRATEGY,
    GIT_NO_WORKFLOW,

    //which environment is the build job in
    //which folder is the build job in
    BUILD_JOB_IS_PROD,
    BUILD_JOB_IS_NON_PROD,

    //Dashboard level
    DASHBOARD_REPO_CONFIGURED,
    DASHBOARD_REPO_NOT_CONFIGURED,
    DASHBOARD_BUILD_CONFIGURED,
    DASHBOARD_BUILD_NOT_CONFIGURED,
    DASHBOARD_CODEQUALITY_CONFIGURED,
    DASHBOARD_CODEQUALITY_NOT_CONFIGURED,
    DASHBOARD_NOT_REGISTERED,

    //whether or not repo and build point to same repo url
    DASHBOARD_REPO_BUILD_VALID,
    DASHBOARD_REPO_BUILD_INVALID,

    //whether or not pr author is same as build job modifier
    DASHBOARD_REPO_PR_AUTHOR_EQ_BUILD_AUTHOR,
    DASHBOARD_REPO_PR_AUTHOR_NE_BUILD_AUTHOR
}
