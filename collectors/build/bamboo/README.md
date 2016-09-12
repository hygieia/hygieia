# Hygieia Build Collectors / Bamboo (Community Contribution)

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

## Building and Deploying

To package the collector into an executable JAR file, run:
```bash
mvn install
```

Copy this file to your server and launch it using:
```bash
java -JAR bamboo-build-collector.jar
```

## application.properties

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the Hudson collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

### Sample application.properties file

```properties
# Database Name
dbname=dashboard

# Database HostName - default is localhost
dbhost=localhost

# Database Port - default is 27017
dbport=9999

# Database Username - default is blank
dbusername=db

# Database Password - default is blank
dbpassword=dbpass

# Collector schedule (required)
bamboo.cron=0 0/5 * * * *

# Jenkins server (required) - Can provide multiple
bamboo.servers[0]=http://bamboo.company.com

# If using username/token for api authentication
#   (required for Cloudbees Jenkins Ops Center) see sample
bamboo.servers[1]=http://username:token@bamboo.company.com

# Another option: If using same username/password Jenkins auth,
#   set username/apiKey to use HTTP Basic Auth (blank=no auth)
bamboo.username=
bamboo.apiKey=

# Determines if build console log is collected - defaults to false
#   (Bamboo for some reason hasn't exposed it as an api...)
bamboo.saveLog=false
```
