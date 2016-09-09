# Hygieia SCM Collectors / Bitbucket (Community Contribution)

Collect source code details from Bitbucket based on URL and branch. Provides implementations for both Bitbucket Cloud (formerly known as Bitbucket) and Bitbucket Server (formerly known as Stash).

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

## Building and Deploying

To package the collector into an executable JAR file, run:
```bash
mvn install
```

Copy this file to your server and launch it using:
```bash
java -jar bitbucket-collector.jar
```

## application.properties

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the Bitbucket collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

### Sample application.properties file

```properties
# Database Name
spring.data.mongodb.dbname=dashboarddb

# Database HostName - default is localhost
spring.data.mongodb.host=

# Database Port - default is 27017
spring.data.mongodb.port=

# Database Username - default is blank
spring.data.mongodb.username=

# Database Password - default is blank
spring.data.mongodb.password=

# Collector schedule (required)
git.cron=0 0/5 * * * *

# mandatory:
git.host=mybitbucketrepo.com/
git.api=/rest/api/1.0/

# Maximum number of days to go back in time when fetching commits
git.commitThresholdDays=15

# Page size for rest calls.
#   Only applicable to Bitbucket Server.
#   Only applicable to Bitbucket Cloud.
git.pageSize=25

# Bitbucket product
#   Set to "cloud" to use Bitbucket Cloud (formerly known as Bitbucket)
#   Set to "server" to use Bitbucket Server (formerly known as Stash)
#   More information can be found here:
#     https://github.com/capitalone/Hygieia/issues/609
git.product=server
```
