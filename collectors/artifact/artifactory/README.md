# Hygieia Artifact Collectors / Artifactory

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

## Building and Deploying

To package the collector into an executable JAR file, run:
```
mvn install
```

Copy this file to your server and launch it using :
```
java -jar artifactory-collector.jar
```

## application.properties

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the Artifactory collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

### Sample application.properties file

```properties
# Database Name
dbname=dashboard

# Database HostName - default is localhost
dbhost=192.168.33.11

# Database Port - default is 27017
dbport=27017

# Database Username - default is blank
dbusername=db

# Database Password - default is blank
dbpassword=dbpass

# Collector schedule (required)
artifactory.cron=0 0/5 * * * *

# Artifactory server (required) - Can provide multiple
artifactory.servers[0]=https://www.jfrog.com/artifactory/

# Artifactory user name (required)
artifactory.username=bobama

# Artifactory api key (required)
artifactory.apiKey=s3cr3t

# The repo to collect artifacts from (required) - Can provide multiple (comma separated for each server) 
artifactory.repos[0]=prerelease,release

# Artifactory REST endpoint
artifactory.endpoint=artifactory/
```
