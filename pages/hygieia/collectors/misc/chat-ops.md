---
title: ChatOps Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: chat-ops.html
---
Configure the ChatOps Collector to display and monitor information (related to information sharing and team collaboration) on the Hygieia Dashboard, from the chat application. The ChatOps Collector works differently in comparison to other collectors; you should only run it once for the widget to register. 

The ChatOps Collector is tested for Enterprise Hipchat version, but supports public HipChat as well. Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies.

Hygieia supports the following chat applications:
 - HipChat
 - Slack.com (Not implemented yet, looking for community to contribute)
 - Gitter.im (Not implemented yet, looking for community to contribute)
 
### Setup Instructions

## Fork and Clone the Collector 

Fork and clone the ChatOps Collector from the [GitHub repo](https://github.com/Hygieia/hygieia-misc-chatops-collector). 

To configure the ChatOps Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `hygieia-misc-chatops-collector` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[username]\hygieia-misc-chatops-collector
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```bash
mvn install
```

The output file `[collector name].jar` is generated in the `hygieia-misc-chatops-collector\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the ChatOps Collector.

To configure parameters for the ChatOps Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `[collector name].jar` file, change directory to `hygieia-misc-chatops-collector\target`, and then execute the following from the command prompt:

```bash
java -jar [collector name].jar --spring.config.name=chatops --spring.config.location=[path to application.properties file]
``` 

### Sample Application Properties File

The sample `application.properties` file lists parameters with sample values to configure the ChatOps Collector. Set the parameters based on your environment setup.

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
		logging.file=./logs/chatops.log

		chatops.cron=5 * * * * *
```

