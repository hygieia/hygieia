---
title: jenkins-audit Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: jenkins-audit.html
---

Configure the Jenkins-audit Collector to collect audit review statuses of Hygieia Dashboards. Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies. 

### Setup Instructions

To configure the Jenkins-audit Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `jenkins-audit` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[usernname]\hygieia\collectors\build\jenkins-audit
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```
 mvn install
```

The output file `jenkins-audit.jar` is generated in the `jenkins-audit\target` folder.

Once the build is run, the audit artefacts(for example, test.exec) should be available at the following path:
```
C:\path\to\junit\findbugs\pmd\checkstyle\jacoco artefacts
```

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the Jenkins-audit Collector.

To configure parameters for the Jenkins-audit Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `jenkins-audit.jar` file, change directory to `jenkins-audit\target`, and then execute the following from the command prompt:

```bash
java -jar jenkins-audit.jar --spring.config.name=jenkins-audit --spring.config.location=[path to application.properties file]
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
		jenkins-audit.cron=0 0/5 * * * *

		# Collector servers (required) - can be multiple servers
		jenkins-audit.servers[0]=https://jenkins.company.com

		# Collector types (not required, but regex should match only the type specified)
		jenkins-audit.artifactRegex.junit=TEST-.*\\.xml
		jenkins-audit.artifactRegex.findbugs=findbugsXml.xml
		jenkins-audit.artifactRegex.pmd=pmd.xml
		jenkins-audit.artifactRegex.checkstyle=checkstyle-result.xml
		jenkins-audit.artifactRegex.jacoco=jacoco.xml

		# Collector job depth (required) should be set to at least 1, and more if you use folder jobs, and so on.
		jenkins-audit.jobDepth=4
```
