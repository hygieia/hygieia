---
title: Hygieia Rally Collector
tags:
keywords:
summary:
sidebar: 
permalink: 
---

Configure the Rally Collector to display the details of the current iteration of the configured project in Hygieia dashboard. Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies.

### Setup Instructions

To configure the Rally Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `rally` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[username]\hygieia\collectors\feature\rally
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```bash
mvn install
```

The output file `rally-collector.jar` is generated in the `rally\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the Rally Collector.

To configure parameters for the Rally Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `rally-collector.jar` file, change directory to `rally\target`, and then execute the following from the command prompt:

```bash
java -jar rally-collector.jar --spring.config.name=rally --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

The sample `application.properties` file lists parameters with sample values to configure the Nexus IQ Collector. Set the parameters based on your environment setup.

```properties
		# Database Name
		dbname=dashboarddb

		# Database HostName - default is localhost
		dbhost=10.0.1.1

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
		rally.cron=0 0/5 * * * *

		# Nexus IQ server(s) (required) - Can provide multiple
		rally.servers[0]=http://rallydev.com

		# Nexus IQ username/password - with read-access to all reports, and so on.
		rally.username[0]=myrallyuserid
		rally.password[0]=myrallypassword

		# If your rally needs to connect through a proxy please specify the HTTP & HTTPS proxy using below attributes.(Optional)
		rally.httpProxyPort=
		rally.httpProxyHost=
		rally.httpsProxyPort=
		rally.httpsProxyHost=
```		