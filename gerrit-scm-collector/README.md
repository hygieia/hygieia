GerritCodeCollector
=========================

Collect source code details from Gerrit based on project and branch

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

Building and Deploying
--------------------------------------

Run
```
mvn install
```
to package the collector into an executable JAR file. Copy this file to your server and launch it using :
```
java -JAR gerrit-scm-collector.jar
```
You will need to provide an **application.properties** file that contains information about how
to connect to the Dashboard MongoDB database instance, as well as properties the Github collector requires. See
the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files)
for information about sourcing this properties file.

###Sample application.properties file
--------------------------------------
    #Database Name 
    database=dashboarddb

    #Database HostName - default is localhost
    dbhost=10.0.1.1

    #Database Port - default is 27017
    dbport=9999

    #Database Username - default is blank
    dbusername=dashboarduser

    #Database Password - default is blank
    dbpassword=dbpass

    #Collector schedule (required)
    gerrit.cron=1 * * * * *
    
    gerrit.host=http://mygerrit.com
    gerrit.user=
    gerrit.password=
    
    #fetch commits from 10 minutes earlier than the runtime to catch anything missed. 
    #Should adjust this depending on how long each run takes.
    gerrit.collectionOffsetMins=10
    
    #when run first time, fetch data for a specified number of past days
    gerrit.firstRunHistoryDays=14
   
