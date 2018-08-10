---
title: GitHub Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: github.html
---
Configure the GitHub Collector to display and monitor information (related to code contribution activities) on the Hygieia Dashboard, from the GitHub repository. Collect source code details from GitHub based on the repository URL and Branch for which you are configuring the collector. 

Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies.

### Setup Instructions

To configure the GitHub Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `github` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```bash
cd C:\Users\[username]\hygieia\collectors\scm\github
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable jar file:

```bash
mvn install
```

The output file `github-collector.jar` is generated in the `github\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the GitHub Collector.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

To configure parameters for the GitHub Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

*   **Step 4: Deploy the Executable File**

To deploy the `github-collector.jar` file, change directory to `github\target`, and then execute the following from the command prompt:

```
java -jar github-collector.jar --spring.config.name=github --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

The sample `application.properties` file lists parameter values to configure the GitHub Collector. Set the parameters based on your environment setup.

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
		logging.file=./logs/github.log

		# Collector schedule (required)
		github.cron=0 0/5 * * * *

		github.host=github.com

		# Maximum number of previous days from current date, when fetching commits
		github.commitThresholdDays=15

    # Optional: Error threshold count after which collector stops collecting for a collector item. Default is 2.
    github.errorThreshold=1

    # This is the key generated using the Encryption class in core
    github.key=<your-generated-key>

    # Personal access token generated from github and used for making authentiated calls
    github.personalAccessToken=
    
    # Github repository Connect Timeout value in milliseconds, default value is 20000 (20s)
    github.connectTimeout=
    
    # Github repository Read Timeout value in milliseconds, default value is 20000 (20s) 
    github.readTimeout=
```
**Note**: For information on generating your GitHub key for private repos, refer to [Encryption of Private Repos](../../setup.md#encryption-for-private-repos).
