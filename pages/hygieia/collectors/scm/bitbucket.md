---
title: Bitbucket Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: bitbucket.html
---

Configure the Bitbucket Collector to display and monitor information (related to code contribution activities) on the Hygieia Dashboard, from the Bitbucket repository. Collect source code details from Bitbucket based on the repository URL and Branch for which you are configuring the collector. Bitbucket Collector provides implementations for both Bitbucket Cloud (formerly known as Bitbucket) and Bitbucket Server (formerly known as Stash).

Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies.

### Setup Instructions

To configure the Bitbucket Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `bitbucket` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[username]\hygieia\collectors\scm\bitbucket
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```bash
mvn install
```

The output file `bitbucket-collector.jar` is generated in the `bitbucket\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the Bitbucket Collector.

To configure parameters for the Bitbucket Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `bitbucket-collector.jar` file, change directory to `bitbucket\target`, and then execute the following from the command prompt:

```bash
java -jar bitbucket-collector.jar --spring.config.name=git --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

The sample `application.properties` file lists parameters with sample values to configure the Bitbucket Collector. Set the parameters based on your environment setup.

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
		logging.file=./logs/bitbucket.log

		# Collector schedule (required)
		git.cron=0 0/5 * * * *

		# Mandatory parameters
		git.host=mybitbucketrepo.com/
		git.api=/rest/api/1.0/

		# Maximum number of days to go back in time when fetching commits
		git.commitThresholdDays=15

		# Page size for rest calls
		#   Only applicable to Bitbucket Server.
		#   Only applicable to Bitbucket Cloud.
		git.pageSize=25

		# Bitbucket product
		#   Set to 'cloud' to use Bitbucket Cloud (formerly known as Bitbucket)
		#   Set to 'server' to use Bitbucket Server (formerly known as Stash)
		#   More information can be found here:	
			https://github.com/capitalone/Hygieia/issues/609
		git.product=server
		
		# Bitbucket key for private repos
		bitbucket.key=<your-generated-key>
```
**Note**: For information on generating your Bitbucket key for private repos, refer to [Encryption for Private Repos](../collectors.md#encryption-for-private-repos).