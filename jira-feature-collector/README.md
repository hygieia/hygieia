#Jira Feature Collector
Retrieves feature content data from the source system APIs and places it in a MongoDB for later retrieval and use by the DevOps Dashboard

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

Building and Deploying
--------------------------------------

Run mvn install to package the collector into an executable JAR file. Copy this file to your server and launch it using
java -JAR jira-feature-collector.jar. You will need to provide an application.properties file that contains information about how
to connect to the Dashboard MongoDB database instance, as well as properties the Jira feature collector requires. See
the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files)
for information about sourcing this properties file.

###Sample application.properties file
--------------------------------------

    #Database Name - default is test
    spring.data.mongodb.database=dashboarddb

    #Database HostName - default is localhost
    spring.data.mongodb.host=10.0.1.1

    #Database Port - default is 27017
    spring.data.mongodb.port=9999

    #Database Username - default is blank
    spring.data.mongodb.username=db

    #Database Password - default is blank
    spring.data.mongodb.password=dbpass

    #Collector schedule (required)
    feature.cron=0 * * * * *

    #Page size for data calls (Jira maxes at 1000)
    feature.pageSize=1000

    #In-built folder housing prepared REST queries (required)
    feature.queryFolder=jiraapi-queries

    #Jira API Query file names (String template requires the files to have .st extension) (required)
    feature.storyQuery=story
    feature.epicQuery=epic
    feature.projectQuery=projectinfo
    feature.memberQuery=memberinfo
    feature.sprintQuery=sprintinfo
    feature.teamQuery=teaminfo
    feature.trendingQuery=trendinginfo

    #Jira Connection Details
    feature.jiraProxyUrl=http://proxy.com
    feature.jiraProxyPort=9999

    # Trending Query:  Number of days in a sprint (not-required)
    feature.sprintDays=60
    # Trending Query:  Length of sprint week (not-required)
    feature.sprintEndPrior=7

    #Scheduled Job prior minutes to recover data created during execution time (usually, 2 minutes is enough)
    feature.scheduledPriorMin=2

    #Delta change date that modulates the collector item task - should be about as far back as possible, in ISO format (required)
    feature.deltaCollectorItemStartDate=2008-01-01T00:00:00.000000

    #Jira Connection Details
    feature.jiraBaseUrl=http://jira-instance.com/
    feature.jiraQueryEndpoint=rest/api/2/
    #64-bit encoded credentials with the pattern username:password
    feature.jiraCredentials=YWxsIHlvdXIgYmFzZSBhcmUgYmVsb25nIHRvIHVzOiB5b3UgYXJlIG9uIHRoZSB3YXkgdG8gZGVzdHJ1Y3Rpb246IG1ha2UgeW91ciB0aW1l
    #OAuth2.0 token credentials (currently not supported in this version)
    feature.jiraOauthAuthtoken=sdfghjkl==
    feature.jiraOauthRefreshtoken=sdfagheh==
    feature.jiraOauthRedirecturi=uri.this.is.test:uri
    feature.jiraOauthExpiretime=234567890987

    #Start dates from which to begin collector data, if no other data is present - usually, a month back is appropriate (required)
    feature.deltaStartDate=2015-03-01T00:00:00.000000
    feature.masterStartDate=2008-01-01T00:00:00.000000
