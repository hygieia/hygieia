GitHubSourceCodeCollector
=========================

Collect source code details from GitHub based on URL and branch

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

Building and Deploying
--------------------------------------

Run mvn install to package the collector into an executable JAR file. Copy this file to your server and launch it using
java -JAR github-collector.jar. You will need to provide an application.properties file that contains information about how
to connect to the Dashboard MongoDB database instance, as well as properties the Github collector requires. See
the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files)
for information about sourcing this properties file.

###Sample application.properties file
--------------------------------------

    #Database Name 
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
    github.cron=0 0/5 * * * *

    github.host=github.com

    #Maximum number of days to go back in time when fetching commits
    github.commitThresholdDays=15

    #Encryption key, this is used for encrypting / decrypting git passwords
    github.key=xFj9m3rf5d9YW8EHqyBUKhmKklcWW60L

###Note on Encryption
For private repositories there is a need to store a github user account with encrypted credentials
There is a tool that now assists with the creation of this encryption information check out the tool project

