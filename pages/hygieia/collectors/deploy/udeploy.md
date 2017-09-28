---
title: uDeploy Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: udeploy.html
---

Configure the uDeploy Collector to display and monitor information (related to application deployments) on the Hygieia Dashboard, from uDeploy. Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies.

### Setup Instructions

To configure the uDeploy Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `udeploy` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[usernname]\hygieia\collectors\scm\udeploy
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```
mvn install
```

The output file `udeploy-collector.jar` is generated in the `udeploy\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the uDeploy Collector.

To configure parameters for the uDeploy Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `udeploy-collector.jar` file, change directory to `udeploy\target`, and then execute the following from the command prompt:

```bash
java -jar udeploy-collector.jar --spring.config.name=udeploy --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

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
		logging.file=./logs/udeploy.log

		# Collector schedule (required)
		udeploy.cron=0 0/5 * * * *

		# uDeploy server (required) - Can provide multiple
		udeploy.servers[0]=http://udeploy.company.com

		# uDeploy user name (required)
		udeploy.username=bobama

		# uDeploy password (required)
		udeploy.password=s3cr3t

		# uDeploy token can be used instead of username and password
		udeploy.token=theudeploytoken
```
