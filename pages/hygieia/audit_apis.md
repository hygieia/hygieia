---
title: About Audit APIs
tags: 
type: 
homepage: 
toc: true
sidebar: hygieia_sidebar
permalink: audit_apis.html
---

Hygieia audit APIs are a collection of API endpoints that serve to audit CI/CD data gathered by Hygieia collectors. The collectors create a large amount of information that provides insights in to the quality of code that goes into production.

## Auditing Capabilities in Release Management

The Audit APIs position Hygieia as the auditor for automated releases. Hygieia provides insights into not only the health of the CI/CD pipeline but also the quality of code going into production. This information enables Hygieia to audit the automated releases.

Hygieia audit APIs collect all the information necessary for a release review. Consider the following scenarios:

- There are security violations in artifacts for deployment
- There is an open source library that includes the wrong kind of license
- Automated test coverage is below the standard

All these cases would result in the release being automatically rejected.

The audit APIs provide endpoints to audit individual widgets on the dashboard. In addition to these endpoints, Hygieia also provides a dashboard-level audit API for analyzing the code quality.

The audit API logic adds various audit flags depending on the data. For a detailed listing of the audit flags, see the audit-api module’s [model]( https://github.com/capitalone/Hygieia/tree/master/api-audit/src/main/java/com/capitalone/dashboard/model) package.

For instructions on installing and running the audit APIs, see the [Setup Instructions](api-audit/api-audit.md) documentation.

For detailed information on audit APIs, see the Swagger documentation, which is generated as part of your build. You can view Swagger documentation on your web browser using the URL,  ```http://localhost:<port>/apiaudit/swagger/index.html#/```.

The following audit APIs support code quality checks:

- Remote Create and Update 
- Dashboard Review
- Peer review
- Static Code Analysis
- Performance Analysis

## Remote Create and Update

These endpoints are used to post (either create or update) dashboard data.

Note: Widget display on UI will show information basing on last updated timestamp.  


**Input Parameters**

- Build entries
- Code repo entries
- Deployment entries
- Feature entries
- Functional test entries
- Library scan entries
- Dashboard metadata
- Security scan entries
- Static code entries

**Sample Request**

```
{"codeRepoEntries":[{"toolName":"GitHub","description":"Brief description","options":{"branch":"master","url":"","personalAccessToken":""}}],"staticCodeEntries":[{"toolName":"Sonar","description":"","options":{"projectName":"","projectId":"","instanceUrl":""}}],"metaData":{"applicationName":"","businessApplication":"","businessService":"","componentName":"","owner":{"authType":"LDAP","username":"username"},"template":"Template","title":"title","type":"Team"}}
```

**API Response**

- SCA data created or updated.

## Dashboard Review Audit API

This endpoint validates that your artifact is meeting the quality gate threshold established in Sonar and returns an appropriate audit status based on whether the threshold has been met or not.

**Input Parameters**

- Date Range (begin date and end date)
- Repo
- Branch Name

**Sample Request**

```
/apiaudit/dashboardReview?title=testSCA&beginDate=1524501989477&endDate=1527598806000&auditType=ALL

#Values for auditType
auditType="ALL" or "CODE_REVIEW" or "BUILD_REVIEW" or "CODE_QUALITY" or "TEST_RESULT" or "PERF_TEST"
```

**API Response**

Passed Validation – The quality gate threshold is met.
Failed Validation – The quality gate threshold is either not met or is missing.

## Peer Review Audit API

The peer review audit API returns the audit status as passed or failed for the peer review of a pull request, based on the following checks:

-	Direct commits are not merged to the master (release) branch
-	Any change made to the master (release) branch is reviewed by a second person before it is merged in to the repository

**Input Parameters**

- Date Range (begin date and end date)
- Repo 
- SCM Name
- Branch Name

**Sample request**

```
/apiaudit/peerReview?repo=https://github.com&branch=master&beginDate=0&endDate=1519415217000
```

**API Response**
 
- Passed Validation - Pull Request peer-reviewed before being merged to the master (release) branch.
- Failed Validation - Pull Request not peer-reviewed or there are direct commits to the master (release) branch.

## Static Code Analysis (SCA)

Static Code Analysis validates that your artifact is meeting the quality gate threshold established in Sonar and returns an appropriate audit status based on whether the threshold has been met or not. Static code analysis validation is achieved using following endpoints:

- Code Quality Audit
- Quality Profile Validation

### Code Quality Audit

Code Quality Audit validates that the Static Code Analysis threshold (set by an individual team) is met during an SCA scan on the release build. This API performs the following requirement checks:

- SCA scan is performed on the latest commit
- SCA Quality Gate and threshold exist for the release build
- Release build meets the threshold set by the team

**Input Parameters**

- Business Application
- Business Service
- Jenkins Sonar Job link
- Project name
- Date Range

**API Response**

- Passed Validation - The SCA threshold is met for all the requirement checks.
- Failed validation - The SCA threshold is not met.

### Code Quality Profile Validation

Code Quality Profile audit API validates that the change author of the quality profile is different from the commit author, based on the following checks:

- Any change made to the SCA Sonar profile (changes to rules, quality gate, or threshold)
- The audit history for details of the change author in case there are SCA Sonar profile changes

**Input Parameters**

- Business Application
- Business Service
- Jenkins Sonar Job link
- Project name
- Date Range

**API Response**

Passed validation – The commit author has not made changes to the SCA Sonar profile (quality profile).
Failed validation – The commit author has modified the SCA Sonar profile (quality profile) during the specified time duration (date range).

**Sample Request for SCA**

```
"staticCodeEntries":[{"toolName":"Sonar","description":"","options":{"projectName":"","projectId":"","instanceUrl":""}}]
```

## Performance Analysis

Periodic performance testing is important to assess the resiliency of an application. This audit API ensures that each release build must have performance testing done and the results of this must meet the passing threshold defined by the team lead.

This API performs the following requirement checks:

- Must execute automated performance test suite for each release build
- Performance test results must meet the pass threshold for each release build

**Input Parameters**

- Business Application
- Business Service
- Date Range

**API Response**

Passed Validation – The performance threshold is met.
Failed Validation – The performance threshold is either not met or is missing.

