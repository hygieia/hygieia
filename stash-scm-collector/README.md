StashSourceCodeCollector
=========================

Collect source code details from stash based on URL and branch

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

Building and Deploying
--------------------------------------

Run mvn install to package the collector into an executable JAR file. Copy this file to your server and launch it using
java -jar stash-collector.jar. You will need to provide an application.properties file that contains information about how
to connect to the Dashboard MongoDB database instance, as well as properties the Stash collector requires. See
the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files)
for information about sourcing this properties file.

###Sample application.properties file
--------------------------------------

    #Database Name 
    spring.data.mongodb.database=dashboarddb

    #Database HostName - default is localhost
    spring.data.mongodb.host=

    #Database Port - default is 27017
    spring.data.mongodb.port=

    #Database Username - default is blank
    spring.data.mongodb.username=

    #Database Password - default is blank
    spring.data.mongodb.password=

    #Collector schedule (required)
    git.cron=0 0/5 * * * *

    #mandatory
    git.host=mystashrepo.com/
    
    #mandatory
    git.api=/rest/api/1.0/

    #Maximum number of days to go back in time when fetching commits
    git.commitThresholdDays=15
