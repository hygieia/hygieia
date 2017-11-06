---
title: HPSM Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: hpsm.html
---
Configure the HPSM Collector to display and monitor information (related to configuration management) on the Hygieia Dashboard, from HP Service Manager. Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies.

### Setup Instructions

To configure the HPSM Collector, execute the following steps:

*   **Step 1: Change Directory**

	Change the current working directory to the `hpsm` directory of your Hygieia source code installation.

	For example, in the Windows command prompt, run the following command:

	```
	cd C:\Users\[username]\hygieia\collectors\scm\hpsm
	```

*   **Step 2: Run Maven Build**

	Run the maven build to package the collector into an executable JAR file:

	```bash
	mvn install
	```

	The output file `hpsm-collector.jar` is generated in the `hpsm\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

	Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the HPSM Collector.

	To configure parameters for the HPSM Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

	For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

	To deploy the `hpsm-collector.jar` file, change directory to `hpsm\target`, and then execute the following from the command prompt:

	```bash
	java -jar hpsm-collector.jar --spring.config.name=hpsm --spring.config.location=[path to application.properties file]
	```

### Sample Application Properties File

The sample `application.properties` file lists parameters with sample values to configure the HPSM Collector. Set the parameters based on your environment setup.

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
		logging.file=./logs/hpsm.log

		#Collector schedule (required)
		hpsm.cron=* * 23 * * *

		#API Details
		hpsm.server=[Your server URL]
		hpsm.port=[Your server port]
		hpsm.protocol=http
		hpsm.resource=SM/7/ws/
		hpsm.contentType=text/xml
		hpsm.charset=UTF-8

		#API Username and Password
		hpsm.user=[Your WSDL Username]
		hpsm.pass=[You WSDL Password]

		#API App Query settings
		hpsm.appSubType=[Your APP configuration Subtype]
		hpsm.appType= [Your APP configuration Type. Leave empty if not required]
		hpsm.appStatus=[Status of APPs]

		#API Component Query settings
		hpsm.compSubType=[Your Component Subtype]
		hpsm.compType=[Your Component Type]

		#API App details
		hpsm.detailsRequestType=RetrieveDeviceListRequest
		hpsm.detailsSoapAction=RetrieveList
		
		#Api Environment Query settings
		hpsm.envSubType=Environment
		hpsm.envType=bizservice

		#API Change Order Details
		hpsm.changeOrderRequestType=RetrieveChangeListRequest
		hpsm.changeOrderSoapAction=RetrieveList
		
		#Number of days to query in case Change Orders have not been collected previously or the change order collection cannot be found.
		hpsm.changeOrderDays=10
		hpsm.changeOrderCron= 0/10 * * * * *
		
		#Default query for change orders may be changed by uncommenting and editing the following line:
		#hpsm.changeOrderQuery=(date.entered > ''{0}'' and date.entered < ''{1}'') or (close.time > ''{0}'' and close.time < ''{1}'')

		#API Incident Details
		hpsm.incidentRequestType=RetrieveIncidentListRequest
		hpsm.incidentSoapAction=RetrieveList
		
		#Number of days to query in case Incidents have not been collected previously or the incident collection cannot be found.
		hpsm.incidentDays=10
		hpsm.incidentCron= 0/10 * * * * *
		
		#Default query for incidents may be changed by uncommenting and editing the following line:
		#hpsm.incidentQuery=(Severity=1 or Severity=2 or Severity=3 or Severity=4) and update.time > ''{0}'' and update.time < ''{1}''
```
