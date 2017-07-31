# Hygieia Build Collectors / Junit/Findbugs/PMD/Jacoco

This project uses Spring Boot to package the collector as an executable JAR with dependencies.
It assumes that the junit/findbugs/pmd/checkstyle/jacoco artefacts are archived in the job

## Building and Deploying

To package the collector into an executable JAR file, run:
```
mvn install
```

Copy this file to your server and launch it using :
```
java -jar jenkins-codequality.jar
```

## application.properties

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the Sonar collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

### Sample application.properties file

```properties
# Database Name
dbname=dashboard

# Database HostName - default is localhost
dbhost=10.0.1.1

# Database Port - default is 27017
dbport=27017

# MongoDB replicaset
dbreplicaset=[false if you are not using MongoDB replicaset]
dbhostport=[host1:port1,host2:port2,host3:port3]

# Database Username - default is blank
dbusername=db

# Database Password - default is blank
dbpassword=dbpass

# Collector schedule (required)
jenkins-codequality.cron=0 0/1 * * * *

# Collector servers (required) - can be multiple
jenkins-codequality.servers[0]=https://jenkins

# Collector types (note not required, but the regex should be match only the type specified)
jenkins-codequality.artifactRegex.junit=TEST-.*\\.xml
jenkins-codequality.artifactRegex.findbugs=findbugsXml.xml
jenkins-codequality.artifactRegex.pmd=pmd.xml
jenkins-codequality.artifactRegex.checkstyle=checkstyle-result.xml
jenkins-codequality.artifactRegex.jacoco=jacoco.xml

# Collector job depth (required) should be set to at least 1, and more if you use folder jobs etc
jenkins-codequality.jobDepth=4

```
