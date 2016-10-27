# Hygieia SCM Collectors / Gitlab (Community Contribution)

Collect source code details from Gitlab based on URL

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

## Building and Deploying

To package the collector into an executable JAR file, run:
```bash
mvn install
```

Copy this file to your server and launch it using:
```
java -JAR gitlab-collector.jar
```

## application.properties

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the Github collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

### Sample application.properties file

```properties
#Database Name 
database=dashboard

#Database HostName - default is IP docker container mongodb
dbhost=localhost

#Database Port - default is 27017
dbport=27017

#Database Username - default is blank
dbusername=db

#Database Password - default is blank
dbpassword=dbpass

#Collector schedule (required)
gitlab.cron=0 0/1 * * * *

#Gitlab host (optional)
gitlab.host=gitlab.company.com

#If your instance of Gitlab is using a self signed certificate, set to true, default is false
gitlab.selfSigned=false

#set apiKey to use HTTPS Auth
gitlab.apiToken=

#Maximum number of days to go back in time when fetching commits
gitlab.commitThresholdDays=15
```

