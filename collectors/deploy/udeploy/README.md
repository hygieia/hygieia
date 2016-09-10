# Hygieia Deploy Collectors / uDeploy

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

## Building and Deploying

To package the collector into an executable JAR file, run:
```bash
mvn install
```

Copy this file to your server and launch it using:
```
java -JAR udeploy-collector.jar
```

## application.properties

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the UDeploy collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

### Sample application.properties file

```properties
# Database Name
spring.data.mongodb.dbname=dashboard

# Database HostName - default is localhost
spring.data.mongodb.host=10.0.1.1

# Database Port - default is 27017
spring.data.mongodb.port=9999

# Database Username - default is blank
spring.data.mongodb.username=db

# Database Password - default is blank
spring.data.mongodb.password=dbpass

# Collector schedule (required)
udeploy.cron=0 0/5 * * * *

# UDeploy server (required) - Can provide multiple
udeploy.servers[0]=http://udeploy.company.com

# UDeploy user name (required)
udeploy.username=bobama

# UDeploy password (required)
udeploy.password=s3cr3t
```
