# Hygieia Security Collectors / Coverity

Coverity finds software issues of several kinds; however, coverity-collector is only intended to gather _security issues_.

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

## Building and Deploying

Run the following command to package the collector into an executable jar. Find the jar file at `target/`.
```
mvn clean install package
```

Copy this file to your server and launch it using:
```
java -jar coverity-collector.jar --spring.config.location=[path to application.properties file]
```

## application.properties

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the Coverity collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

### Sample application.properties file

```properties
# Database Name
dbname=dashboard

# Database HostName - default is localhost
dbhost=10.0.1.1

# Database Port - default is 27017
dbport=27017

# Database Username - default is blank
dbusername=db

# Database Password - default is blank
dbpassword=dbpass


# Collector schedule (required)
coverity.cron=0 0/5 * * * *
```
