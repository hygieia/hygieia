# VersionOne Feature Collector
Retrieves VersionOne feature content data from the source system APIs and places it in a MongoDB for later retrieval and use by the DevOps Dashboard

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

## Building and Deploying
Run

```bash
mvn install
```

to package the collector into an executable JAR file. Copy this file to your server and launch it using :

```bash
java -jar versionone-feature-collector.jar
```

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the VersionOne feature collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

### Sample application.properties file

--------------------------------------------------------------------------------

```properties
# PageSize - Expand contract this value depending on VersionOne implementation's
# default server timeout setting (You will likely receive a SocketTimeoutException)
feature.pageSize=2000

# Delta change date that modulates the collector item task
# Occasionally, these values should be modified if database size is a concern
feature.deltaStartDate=2016-03-01T00:00:00.000000
feature.masterStartDate=2016-03-01T00:00:00.000000
feature.deltaCollectorItemStartDate=2016-03-01T00:00:00.000000
# Chron schedule: S M D M Y [Day of the Week]
feature.cron=0 * * * * *

# VERSIONONE CONNECTION DETAILS:
# Enterprise Proxy - ONLY INCLUDE IF YOU HAVE A PROXY
feature.versionOneProxyUrl=http://proxy.com:9000
feature.versionOneBaseUri=https://versionone.com
feature.versionOneAccessToken=dGhpc2lzYXRva2VuZnJvbXYx

# ST Query File Details - Required, but DO NOT MODIFY
feature.queryFolder=v1api-queries
feature.storyQuery=story
feature.projectQuery=projectinfo
feature.teamQuery=teaminfo
feature.trendingQuery=trendinginfo
```

## Implementation Details:

[TBA]
