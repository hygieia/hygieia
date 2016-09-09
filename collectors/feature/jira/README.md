# Hygieia Feature Collectors / Jira

Retrieves feature content data from the source system APIs and places it in a MongoDB for later retrieval and use by the DevOps Dashboard

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

## Building and Deploying

To package the collector into an executable JAR file, run:
```bash
mvn install
```

Copy this file to your server and launch it using:
```bash
java -jar jira-feature-collector.jar
```

## application.properties

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the Jira feature collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

### Sample application.properties file, with minimum overrides

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
# In Jira, your instance will have its own custom field created for "story points"
# This field allows you to specify that data field for your instance
# of Jira.  Note:  You can retrieve your instance's storypoints ID field name via the following URI where your
# queried user story issue has story points set on it; your custom field name describes the
# story points value you expect to see:
# https://[your-jira-domain-name]/rest/api/2/issue/[some-issue-name]
#############################################################################
feature.jiraStoryPointsFieldName=customfield_10003
```

#### Troubleshooting
##### The jira collector log does not pull data in for XXXX
Verify the jira collector configuration for the custom fields is setup correctly. Hit the rest API outlined in the sample application properties above to see what data is being pulled in. A healthy log will look something like this:
```
2016-09-01 07:27:00,006 INFO c.c.d.collector.CollectorTask - Running Collector: Jira
2016-09-01 07:27:00,010 INFO c.c.d.collector.CollectorTask - -----------------------------------
2016-09-01 07:27:00,011 INFO c.c.d.collector.CollectorTask - https://my.jira.com/
2016-09-01 07:27:00,011 INFO c.c.d.collector.CollectorTask - -----------------------------------
2016-09-01 07:27:02,571 INFO c.c.d.collector.CollectorTask - Team Data 15 2s
2016-09-01 07:27:03,050 INFO c.c.d.collector.CollectorTask - Project Data 15 1s
2016-09-01 07:27:03,752 INFO c.c.d.collector.CollectorTask - Story Data 36 1s
2016-09-01 07:27:03,752 INFO c.c.d.collector.CollectorTask - Finished 4s
```

##### My jira widget dropdown does not show any teams
Verify your jira collector configuration. Verify that the jira collector is pulling in data by observing the logs. Connect to the mongo database using a tool such as RoboMongo and check that the 'feature' collection has data. Verify your API container is configured to hit the correct database.

##### My jira widget shows all 0's for estimates
Verify your jira collector configuration. Verify that the jira collector is pulling in data by observing the logs. Connect to the mongo database using a tool such as RoboMongo and check that the 'feature' collection has data. Check that features associated to an active sprint have the sEstimate (sEstimateTime for hours) field populated.

##### My jira widget only shows kanban sprints
In order to show scrum sprints there must exist stories with sprints attached to them that are active and have a recent start date. You can verify that this information is being pulled by either hitting the rest API or looking into the mongo database in the feature collection.

##### ERROR c.c.d.client.DefaultJiraClient - No result was available from Jira unexpectedly - defaulting to blank response. The reason for this fault is the following:RestClientException{statusCode=Optional.of(403), errorCollections=[]}
This may happen if you have had too many failed login attempts and a CAPTCHA guard has been triggered. Try logging in to jira with a browser successfully to remove the CAPTCHA guard. Verify that the jira credentials are correct.

##### My issue is not listed or has not been resolved
Search active and closed issues on github for 'jira'. Chances are your configuration is wrong and someone else has struggled through fixing it in another issue. Please refrain from commenting on closed issues. Github link: https://github.com/capitalone/Hygieia/issues?q=jira

## Implementation Details:

[TBA]
