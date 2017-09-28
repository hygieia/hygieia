---
title: Sonar Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: sonar.html
---
Configure the Sonar Collector to display and analyze information (related to code quality) on the Hygieia Dashboard, from SonarQube (formerly known as Sonar).
Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies.

### Setup Instructions

To configure the Sonar Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `sonar` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[usernname]\hygieia\collectors\scm\sonar
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```
 mvn install
```

The output file `sonar-collector.jar` is generated in the `sonar\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the Sonar Collector.

To configure parameters for the Sonar Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `sonar-collector.jar` file, change directory to `sonar\target`, and then execute the following from the command prompt:

```
java -jar sonar-collector.jar --spring.config.name=sonar --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

The sample `application.properties` file lists parameters with sample values to configure the Sonar Collector. Set the parameters based on your environment setup.

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
		sonar.cron=0 0/5 * * * *

		# Sonar server(s) (required) - Can provide multiple
		sonar.servers[0]=http://sonar.company.com
		
		# Sonar version, match array index to the server. If not set, will default to version prior to 6.3.
		sonar.versions[0]=6.31
		
		# Sonar Metrics - Required. 
		#Sonar versions lesser than 6.3
		sonar.metrics[0]=ncloc,line_coverage,violations,critical_violations,major_violations,blocker_violations,violations_density,sqale_index,test_success_density,test_failures,test_errors,tests
		
		# For Sonar version 6.3 and above
		sonar.metrics[0]=ncloc,violations,new_vulnerabilities,critical_violations,major_violations,blocker_violations,tests,test_success_density,test_errors,test_failures,coverage,line_coverage,sqale_index,alert_status,quality_gate_details

```
