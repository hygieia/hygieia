---
title: Hygieia Nexus IQ Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: nexus-iq-collector.html
---

Configure the Nexus IQ Collector to display and monitor information (related to library policies) on the Hygieia Dashboard, from Nexus IQ. Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies.

### Setup Instructions

## Fork and Clone the Collector 

Fork and clone the Nexus IQ Collector from the [GitHub repo](https://github.com/Hygieia/hygieia-oss-nexusiq-collector). 

To configure the Nexus IQ Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `hygieia-oss-nexusiq-collector` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[username]\hygieia-oss-nexusiq-collector
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```bash
mvn install
```

The output file `[collector name].jar` is generated in the `hygieia-oss-nexusiq-collector\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the Nexus IQ Collector.

To configure parameters for the Nexus IQ Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `[collector name].jar` file, change directory to `hygieia-oss-nexusiq-collector\target`, and then execute the following from the command prompt:

```bash
java -jar [collector name].jar --spring.config.name=nexusiq --spring.config.location=[path to application.properties file]
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
		nexusiq.cron=0 0/5 * * * *

		# Nexus IQ server(s) (required) - Can provide multiple
		nexusiq.servers[0]=http://nexusiq.company.com

		# Nexus IQ username/password - with read-access to all reports, and so on.
		nexusiq.usernames[0]=mynexusiquserid
		nexusiq.passwords[0]=mynexusiqpassword


		#In case of multiple license violations for a given library, consider the strictest violation
		nexusiq.selectStricterLicense=true
```		
