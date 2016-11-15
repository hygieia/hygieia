# Hygieia Deploy Collectors / XLDeploy (Community Contribution)

This project uses Spring Boot to package the collector as an executable JAR with dependencies. Code created against XL Deploy 5.1.4.

## Building and Deploying

To package the collector into an executable JAR file, run:
```
mvn install
```

Copy this file to your server and launch it using :
```
java -jar xldeploy-collector.jar
```

## application.properties

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the XLDeploy collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

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
xldeploy.cron=0 0/5 * * * *

# XLDeploy server (required) - Can provide multiple
xldeploy.servers[0]=http://xldeploy.company.com

# XLDeploy user name (required) - Can provide multiple
xldeploy.usernames[0]=bobama 

# XLDeploy password (required) - Can provide multiple
xldeploy.passwords[0]=s3cr3t
```
