---
title: Jenkins-codequality Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: jenkins-codequality.html
---

Configure the Jenkins-codequality Collector to display and monitor information (related to code quality) on the Hygieia Dashboard, from Jenkins-codequality. Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies. 

### Setup Instructions

To configure the Jenkins-codequality Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `jenkins-codequality` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[usernname]\hygieia\collectors\build\jenkins-codequality
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```
 mvn install
```

The output file `jenkins-codequality.jar` is generated in the `jenkins-codequality\target` folder.

Once the build is run, the code quality artefacts(for example, test.exec) should be available at the following path:
```
C:\path\to\junit\findbugs\pmd\checkstyle\jacoco artefacts
```

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the Jenkins-codequality Collector.

To configure parameters for the Jenkins-codequality Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `jenkins-codequality.jar` file, change directory to `jenkins-codequality\target`, and then execute the following from the command prompt:

```bash
java -jar jenkins-codequality.jar --spring.config.name=jenkins-codequality --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

```properties
		# Database Name
		dbname=dashboarddb

		# Database HostName - default is localhost
		dbhost=10.0.1.1

		# Database Port - default is 27017
		dbport=27017

		# MongoDB replicaset
		dbreplicaset=[false, if you are not using MongoDB replicaset]
		dbhostport=[host1:port1,host2:port2,host3:port3]

		# Database Username - default is blank
		dbusername=dashboarduser

		# Database Password - default is blank
		dbpassword=dbpassword

		# Collector schedule (required)
		jenkins-codequality.cron=0 0/1 * * * *

		# Collector servers (required) - can be multiple servers
		jenkins-codequality.servers[0]=https://jenkins.company.com

		# Collector types (not required, but regex should match only the type specified)
		jenkins-codequality.artifactRegex.junit=TEST-.*\\.xml
		jenkins-codequality.artifactRegex.findbugs=findbugsXml.xml
		jenkins-codequality.artifactRegex.pmd=pmd.xml
		jenkins-codequality.artifactRegex.checkstyle=checkstyle-result.xml
		jenkins-codequality.artifactRegex.jacoco=jacoco.xml

		# Collector job depth (required) should be set to at least 1, and more if you use folder jobs, and so on.
		jenkins-codequality.jobDepth=4
```
