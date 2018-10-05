---
title: nfrr-audit Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: nfrr-audit.html
---

Configure the nfrr-audit Collector to collect audit review statuses of Hygieia Dashboards. Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies. 

### Setup Instructions

To configure the nfrr-audit Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `nfrr-audit` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[usernname]\hygieia\collectors\build\nfrr-audit
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```
 mvn install
```

The output file `nfrr-audit.jar` is generated in the `nfrr-audit\target` folder.

Once the build is run, the audit artefacts(for example, test.exec) should be available at the following path:
```
C:\path\to\junit\findbugs\pmd\checkstyle\jacoco artefacts
```

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the nfrr-audit Collector.

To configure parameters for the nfrr-audit Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `nfrr-audit.jar` file, change directory to `nfrr-audit\target`, and then execute the following from the command prompt:

```bash
java -jar nfrr-audit.jar --spring.config.name=nfrr-audit --spring.config.location=[path to application.properties file]
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
		nfrr.cron=0 0/5 * * * *

		# Collector servers (required) - can be multiple servers
		nfrr.servers[0]=https://nfrr.company.com

		# Collector types (not required, but regex should match only the type specified)
		nfrr.artifactRegex.junit=TEST-.*\\.xml
		nfrr.artifactRegex.findbugs=findbugsXml.xml
		nfrr.artifactRegex.pmd=pmd.xml
		nfrr.artifactRegex.checkstyle=checkstyle-result.xml
		nfrr.artifactRegex.jacoco=jacoco.xml

		# Collector job depth (required) should be set to at least 1, and more if you use folder jobs, and so on.
		nfrr.jobDepth=4
```
