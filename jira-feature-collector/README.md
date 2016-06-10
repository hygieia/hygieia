# Jira Feature Collector
Retrieves feature content data from the source system APIs and places it in a MongoDB for later retrieval and use by the DevOps Dashboard

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

## Building and Deploying
Run

```bash
mvn install
```

to package the collector into an executable JAR file. Copy this file to your server and launch it using :

```bash
java -jar jira-feature-collector.jar
```

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the Jira feature collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

### Sample application.properties file, with minimum overrides

--------------------------------------------------------------------------------

```properties
# PageSize - Expand contract this value depending on Jira implementation's
# default server timeout setting (You will likely receive a SocketTimeoutException)
feature.pageSize=100

# Delta change date that modulates the collector item task
# Occasionally, these values should be modified if database size is a concern
feature.deltaStartDate=2016-03-01T00:00:00.000000
feature.masterStartDate=2016-03-01T00:00:00.000000
feature.deltaCollectorItemStartDate=2016-03-01T00:00:00.000000
# Chron schedule: S M D M Y [Day of the Week]
feature.cron=0 * * * * *

# ST Query File Details - Required, but DO NOT MODIFY
feature.queryFolder=jiraapi-queries
feature.storyQuery=story
feature.epicQuery=epic

# JIRA CONNECTION DETAILS:
# Enterprise Proxy - ONLY INCLUDE IF YOU HAVE A PROXY
feature.jiraProxyUrl=http://proxy.com
feature.jiraProxyPort=9000
feature.jiraBaseUrl=https://jira.com/
feature.jiraQueryEndpoint=rest/api/2/
# For basic authentication, requires username:password as string in base64
# This command will make this for you:  echo -n username:password | base64
feature.jiraCredentials=dXNlcm5hbWU6cGFzc3dvcmQ=
# OAuth is not fully implemented; please blank-out the OAuth values:
feature.jiraOauthAuthtoken=
feature.jiraOauthRefreshtoken=
feature.jiraOauthRedirecturi=
feature.jiraOauthExpiretime=

#############################################################################
# In Jira, general IssueType IDs are associated to various "issue"
# attributes. However, there is one attribute which this collector's
# queries rely on that change between different instantiations of Jira.
# Please provide a string name reference to your instance's IssueType for
# the lowest level of Issues (e.g., "user story") specific to your Jira
# instance.  Note:  You can retrieve your instance's IssueType Name
# listings via the following URI:  https://[your-jira-domain-name]/rest/api/2/issuetype/
#############################################################################
feature.jiraIssueTypeId=Story

#############################################################################
# In Jira, your instance will have its own custom field created for "sprint" or "timebox" details,
# which includes a list of information.  This field allows you to specify that data field for your
# instance of Jira. Note: You can retrieve your instance's sprint data field name
# via the following URI, and look for a package name com.atlassian.greenhopper.service.sprint.Sprint;
# your custom field name describes the values in this field:
# https://[your-jira-domain-name]/rest/api/2/issue/[some-issue-name]
#############################################################################
feature.jiraSprintDataFieldName=customfield_10000

#############################################################################
# In Jira, your instance will have its own custom field created for "super story" or "epic" back-end ID,
# which includes a list of information.  This field allows you to specify that data field for your instance
# of Jira.  Note:  You can retrieve your instance's epic ID field name via the following URI where your
# queried user story issue has a super issue (e.g., epic) tied to it; your custom field name describes the
# epic value you expect to see, and is the only field that does this for a given issue:
# https://[your-jira-domain-name]/rest/api/2/issue/[some-issue-name]
#############################################################################
feature.jiraEpicIdFieldName=customfield_10002

#############################################################################
# Internal Status Mappings - THESE SHOULD BE FILLED OUT FOR EVERY CUSTOM STATUS VALUE
# IN YOUR JIRA INSTANCE
#
# Use the following API call to get all of your status mappings:  http://jira.your.instance.com/rest/api/2/status/
#############################################################################
feature.todoStatuses[0]=
feature.doingStatuses[0]=
feature.doneStatuses[0]=

# Status mappings, E.g.:
#
# feature.todoStatuses[0]=Open
# feature.todoStatuses[1]=Groom
# feature.todoStatuses[2]=Selected for Development
# feature.todoStatuses[3]=Backlog
# feature.todoStatuses[4]=Grooming
# feature.todoStatuses[5]=ToDo
# feature.todoStatuses[6]=To Do
# feature.todoStatuses[6]=Backlog1
# feature.doingStatuses[0]=Validation
# feature.doingStatuses[1]=Test
# feature.doingStatuses[2]=In Process
# feature.doingStatuses[3]=In Progress
# feature.doingStatuses[4]=Awaiting Approval - 2
# feature.doingStatuses[5]=Change Request - 2
# feature.doneStatuses[0]=Awaiting Approval
# feature.doneStatuses[1]=Done
# feature.doneStatuses[2]=Resolved
```

## Implementation Details:

[TBA]
