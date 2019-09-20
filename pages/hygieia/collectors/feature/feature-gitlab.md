---
title: Gitlab Feature Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: feature-gitlab.html
---

Configure the Gitlab Feature Collector to display and monitor information (related to features/issues) on the Hygieia Dashboard, from Gitlab issue boards. Issue boards were introduced to Gitlab in version 8.11, so you must be on this version of Gitlab or later to take advantage of this functionality.  

This collector retrieves all the issues for your project, and classifies them based on your issue board(s). (Gitlab Enterprise Edition allows you to have multiple boards for a project). By default, Gitlab provides you with two columns on your board, 'Backlog', and 'Done'; you can then customize the columns in between. The collector works by finding the 'lists' you have created for the board, finding all the issues you have that belong to those lists, and then classifying them as 'In Progress'. Any issues which are 'Closed' are classified as 'Done'.

Hygieia's UI has two different ways of displaying issue boards, Kanban or Scrum.  The collector determines whether an issue is Kanban or Scrum based on Gitlab's Milestones. If an issue is associated with a Milestone, and the Milestone also has an end date, then the issue is shown as Scrum, otherwise it is displayed as Kanban. The reason for this is that Scrum has set deadlines, which is represented using Milestones with deadlines. Kanban is just a backlog organized by priority with no end date.  

Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies.

### Setup Instructions

## Fork and Clone the Collector 

Fork and clone the Gitlab Feature Collector from the [GitHub repo](https://github.com/Hygieia/hygieia-feature-gitlab-collector). 

To configure the Gitlab Feature Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `hygieia-feature-gitlab-collector` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```bash
cd C:\Users\[username]\hygieia-feature-gitlab-collector
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```bash
mvn install
```

The output file `[collector name].jar` is generated in the `hygieia-feature-gitlab-collector\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the Gitlab Feature Collector.

To configure parameters for the Gitlab Feature Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `[collector name].jar` file, change directory to `hygieia-feature-gitlab-collector\target`, and then execute the following from the command prompt:

```bash
java -jar [collector name].jar --spring.config.name=feature --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

The sample `application.properties` file lists parameters with sample values to configure the Gitlab Feature Collector. Set the parameters based on your environment setup.

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
		logging.file=./logs/gitlabFeature.log

		#Collector schedule (required)
		gitlab.cron=0 0/1 * * * *

		#Gitlab host (optional, defaults to 'gitlab.com')
		gitlab.host=gitlab.com

		#Gitlab protocol (optional, defaults to 'http')
		gitlab.protocol=http

		#Gitlab port (optional, defaults to protocol default port)
		gitlab.port=80

		#Gitlab path (optional, if your instance of Gitlab requires a path)
		gitlab.path=/gitlab/resides/here
		  
		#Gitlab API Token (required, collector will have permission of user associated to the token)
		#If token is from admin account, will be able to view all teams, and can collect all issues
		#If token is from standard user, will show only teams that user is a part of, and can only collect issues that user could view
		#It is recommended to create a Gitlab account for the collector, using its Access Token, and adding that user to the teams you want to see issues for
		gitlab.apiToken=

		#Gitlab selfSignedCertificate (optional, defaults to false, set to true if your instance of gitlab is running on https without a trusted certificate
		gitlab.selfSignedCertificate=false

		#Gitlab API Version (optional, defaults to current version of 4)
		gitlab.apiVersion=4

```
