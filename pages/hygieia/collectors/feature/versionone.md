---
title: VersionOne Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: versionone.html
---
Configure the VersionOne Collector to display and monitor information (related to features/issues) on the Hygieia Dashboard, from VersionOne issue boards. This collector retrieves VersionOne feature content data from the source system APIs and places it in MongoDB for later retrieval and use by the DevOps Dashboard.

Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies.

### Setup Instructions

## Fork and Clone the Collector 

Fork and clone the VersionOne Collector from the [GitHub repo](https://github.com/Hygieia/hygieia-feature-versionone-collector). 

To configure the VersionOne Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `hygieia-feature-versionone-collector` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[username]\hygieia-feature-versionone-collector
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```bash
mvn install
```

The output file `[collector name].jar` is generated in the `hygieia-feature-versionone-collector\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the VersionOne Collector.

To configure parameters for the VersionOne Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `[collector name].jar` file, change directory to `hygieia-feature-versionone-collector\target`, and then execute the following from the command prompt:

```bash
java -jar [collector name].jar --spring.config.name=feature --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

The sample `application.properties` file lists parameters with sample values to configure the VersionOne Collector. Set the parameters based on your environment setup.

```properties
		# Database Name
		dbname=dashboarddb

		# Database HostName - default is localhost
		dbhost=localhost

		# Database Port - default is 27017
		dbport=27017

		# MongoDB replicaset
		dbreplicaset=[false if you are not using MongoDB replicaset]
		dbhostport=[host1:port1,host2:port2,host3:port3]

		# Database Username - default is blank
		dbusername=dashboarduser

		# Database Password - default is blank
		dbpassword=dbpassword

		# Logging File location
		logging.file=./logs/versionone.log

		# PageSize - Expand contract this value depending on VersionOne implementation's
		# default server timeout setting (You will likely receive a SocketTimeoutException)
		feature.pageSize=2000

		# Delta change date that modulates the collector item task
		# Occasionally, these values should be modified if database size is a concern
		feature.deltaStartDate=2016-03-01T00:00:00.000000
		feature.masterStartDate=2016-03-01T00:00:00.000000
		feature.deltaCollectorItemStartDate=2016-03-01T00:00:00.000000

		#############################################################################
		# Maximum Kanban iteration length allowed for a sprint start/end date before
		# being converted a Kanban generic type iteration.  For example, if you want anything
		# longer than a 3-week sprint to be considered as Kanban in the Feature Widget,
		# change this value to, for example, 36.  Default value is 28 days.
		#
		# Note:  This field otherwise does NOT need to be included, and is commented out
		#############################################################################
		#
		# feature.maxKanbanIterationLength=28

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
