---
title: Jenkins Build Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: jenkins.html
---
Configure the Jenkins Collector to display and monitor information (related to build status) on the Hygieia Dashboard, from Jenkins. Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies.

### Setup Instructions

To configure the Jenkins Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `jenkins` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[usernname]\hygieia\collectors\build\jenkins
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

``` 
mvn install
```

The output file `hudson-collector.jar` is generated in the `jenkins\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the Jenkins Collector.

To configure parameters for the Jenkins Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `hudson-collector.jar` file, change directory to `jenkins\target`, and then execute the following from the command prompt:

```
java -jar jenkins-collector.jar --spring.config.name=jenkins --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

```properties
		# Database Name
		dbname=dashboarddb

		# Database HostName - default is localhost
		dbhost=localhost

		# Database Port - default is 27017
		dbport=9999

		# MongoDB replicaset
		dbreplicaset=[false if you are not using MongoDB replicaset]
		dbhostport=[host1:port1,host2:port2,host3:port3]

		# Database Username - default is blank
		dbusername=dashboarduser

		# Database Password - default is blank
		dbpassword=dbpassword

		# Collector schedule (required)
		jenkins.cron=0 0/5 * * * *

		# The page size
		jenkins.pageSize=1000

		# The folder depth - default is 10
		jenkins.folderDepth=10

		# Jenkins server (required) - Can provide multiple
		jenkins.servers[0]=http://jenkins.company.com

		# If using username/token for API authentication
		# (required for Cloudbees Jenkins Ops Center) For example,
		jenkins.servers[1]=http://username:token@jenkins.company.com

		# Another option: If using same username/password Jenkins auth,
		# set username/apiKey to use HTTP Basic Auth (blank=no auth)
		jenkins.usernames[0]=
		jenkins.apiKeys[0]=

		# Determines if build console log is collected - defaults to false
		jenkins.saveLog=true
```
