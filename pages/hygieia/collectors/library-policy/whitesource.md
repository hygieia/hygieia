---
title: Hygieia WhiteSource Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: 
---

Configure the WhiteSource Collector to display and monitor information (related to library policies) on the Hygieia Dashboard, from WhiteSource. Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies.
Please refer https://whitesource.atlassian.net/wiki/spaces/WD/pages/814612683/HTTP+API+v1.3 for api documentation.

### Setup Instructions

## Fork and Clone the Collector 

Fork and clone the WhiteSource Collector from the [GitHub repo](https://github.com/Hygieia/hygieia-whitesource-collector). 

To configure the WhiteSource Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `hygieia-whitesource-collector` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[username]\hygieia-whitesource-collector
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```bash
mvn install
```

The output file `[collector name].jar` is generated in the `hygieia-whitesource-collector\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the WhiteSource Collector.

To configure parameters for the WhiteSource Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `[collector name].jar` file, change directory to `hygieia-whitesource-collectorr\target`, and then execute the following from the command prompt:

```bash
java -jar [collector name].jar --spring.config.name=whitesource --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

The sample `application.properties` file lists parameters with sample values to configure the WhiteSource Collector. Set the parameters based on your environment setup.

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
		whitesource.cron=0 0/5 * * * *

		# WhiteSource server(s) (required) - Can provide multiple
		whitesource.servers[0]=http://whitesource.company.com

		# WhiteSource userKey - provided for read access
		whitesource.userKey=
		
	    	# WhiteSource orgToken - Organization token to identify an organization in whitesource - Can provide multiple
		whitesource.orgToken[0]=

	    	# WhiteSource sleeTime - can manual inject thread sleep-time between transations to whitesource apis
	    	whitesource.sleepTime=150              

	    	# WhiteSource requestRateLimit - threshold for rate-limit 
	    	whitesource.requestRateLimit=3            

	    	# WhiteSource requestRateLimitTimeWindow
	    	whitesource.requestRateLimitTimeWindow=1000

	    	# WhiteSource errorResetWindow
	    	whitesource.errorResetWindow = 36000

	    	# WhiteSource highLicensePolicyTypes - transalation of license violations to HIGH severity (Enterprise specific) - can be multiple
	    	whitesource.criticalLicensePolicyTypes[0].policyName=
		whitesource.criticalLicensePolicyTypes[0].descriptions[0]=

		whitesource.highLicensePolicyTypes[0].policyName=
		whitesource.highLicensePolicyTypes[0].descriptions[0]=
		

		whitesource.mediumLicensePolicyTypes[0].policyName=
		whitesource.mediumLicensePolicyTypes[0].descriptions[0]=

		whitesource.lowLicensePolicyTypes[0].policyName=
		whitesource.lowLicensePolicyTypes[0].descriptions[0]=
    
```		
