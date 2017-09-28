---
title: XLDeploy Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: xldeploy.html
---

Configure the XLDeploy Collector to display and monitor information (related to application deployments) on the Hygieia Dashboard, from XLDeploy. Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies. The XLDeploy collector is coded for XLDeploy v5.1.4.

### Setup Instructions

To configure the XLDeploy Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `xldeploy` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[usernname]\hygieia\collectors\scm\xldeploy
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```bash
mvn install
```

The output file `xldeploy-collector.jar` is generated in the `xldeploy\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the XLDeploy Collector.

To configure parameters for the XLDeploy Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `xldeploy-collector.jar` file, change directory to `xldeploy\target`, and then execute the following from the command prompt:

```bash
java -jar xldeploy-collector.jar --spring.config.name=xldeploy --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

```properties
		# Database Name
		dbname=dashboarddb

		# Database HostName - default is localhost
		dbhost=192.168.33.11

		# Database Port - default is 27017
		dbport=27017

		# MongoDB replicaset
		dbreplicaset=[false if you are not using MongoDB replicaset]
		dbhostport=[host1:port1,host2:port2,host3:port3]

		# Database Username - default is blank
		dbusername=dashboarduser

		# Database Password - default is blank
		dbpassword=dbpassword

		# Collector schedule (required)
		xldeploy.cron=0 0/5 * * * *

		# XLDeploy server (required) - You can provide multiple servers
		xldeploy.servers[0]=http://xldeploy.company.com

		# XLDeploy username (required) - You can provide multiple usernames
		xldeploy.usernames[0]=bobama 

		# XLDeploy password (required) - You can provide multiple passwords
		xldeploy.passwords[0]=s3cr3t
```
