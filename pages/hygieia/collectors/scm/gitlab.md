---
title: Gitlab Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: gitlab.html
---
Configure the Gitlab Collector to display and monitor information (related to code contribution activities) on the Hygieia Dashboard, from the Gitlab repository. Collect source code details from Gitlab based on the repository URL.

This project uses Spring Boot to package the collector as an executable JAR file with dependencies.

### Setup Instructions

To configure the Gitlab Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `gitlab` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[username]\hygieia\collectors\scm\gitlab
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable jar file:

```
 mvn install
```

The output file `gitlab-collector.jar` is generated in the `gitlab\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the Gitlab Collector.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

To configure parameters for the Gitlab Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

*   **Step 4: Deploy the Executable File**

To deploy the `gitlab-collector.jar` file, change directory to `gitlab\target`, and then execute the following from the command prompt:

```
java -jar gitlab-collector.jar --spring.config.name=gitlab --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

The sample `application.properties` file lists parameter values to configure the Gitlab Collector. Set the parameters based on your environment setup.

``` 
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
		logging.file=./logs/gitlab.log

		#Collector schedule (required)
		gitlab.cron=0 0/1 * * * *

		#Gitlab host (optional, defaults to 'gitlab.com')
		gitlab.host=gitlab.company.com

		#Gitlab protocol (optional, defaults to 'http')
		gitlab.protocol=http

		#Gitlab port (optional, defaults to protocol default port)
		gitlab.port=80

		#Gitlab path (optional, if your instance of gitlab requires a path)
		gitlab.path=/gitlab/resides/here

		#If your instance of Gitlab is using a self-signed certificate, set to true, default is false
		gitlab.selfSignedCertificate=false

		#Gitlab API Token (required, user token the collector will use by default, can be overridden on a per repo basis from the UI. API token provided by Gitlab)
		gitlab.apiToken=

		#Maximum number of previous days from current date, when fetching commits
		gitlab.commitThresholdDays=15
		
		# Gitlab key for private repos
		gitlab.key=<your-generated-key>
```
**Note**: For information on generating your Gitlab key for private repos, refer to [Encryption of Private Repos](../../setup.md#encryption-for-private-repos).
