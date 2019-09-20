---
title: Artifactory Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: artifactory.html
---
Configure the Artifactory Collector to display and monitor information (related to artifact lifecycle management) on the Hygieia Dashboard, from Artifactory. Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies.

### Setup Instructions

## Fork and Clone the Collector 

Fork and clone the Artifactory Collector from the [GitHub repo](https://github.com/Hygieia/hygieia-artifact-artifactory-collector). 

To configure the Artifactory Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `hygieia-artifact-artifactory-collector` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[username]\hygieia-artifact-artifactory-collector
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```bash
mvn install
```

The output file `[collector name].jar` is generated in the `hygieia-artifact-artifactory-collector\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the Artifactory Collector.

To configure parameters for the Artifactory Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `[collector name].jar` file, change directory to `hygieia-artifact-artifactory-collector\target`, and then execute the following from the command prompt:

```bash
java -jar [collector name].jar --spring.config.name=artifactory --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

The sample `application.properties` file lists parameters with sample values to configure the Artifactory Collector. Set the parameters based on your environment setup.

```properties
		# Database Name
		dbname=dashboarddb

		# Database HostName - default is localhost
		dbhost=192.168.33.11

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
		artifactory.cron=0 0/5 * * * *

		# Artifactory server (required) - Can provide multiple
		artifactory.servers[0].url=https://www.jfrog.com/artifactory/
		
		# Artifactory REST endpoint
		artifactory.endpoint=artifactory/

		# Artifactory server - username
		artifactory.servers[0].username= bobama
		
		# Artifactory server - apiKey
		artifactory.servers[0].apiKey= s3cr3t
		
		# Artifactory server - repository 
		artifactory.servers[0].repoAndPatterns[0].repo=repo1
		
		# Artifactory server - patterns - could be multiple
		# Artifact Regex Patterns
		# Each artifact found is matched against the following patterns in order (first one wins)
		# The following capture groups are available:
		#  - group
		#  - module
		#  - artifact
		#  - version
		#  - classifier
		#  - ext
		
		# Matches maven artifacts of the form [org]/[module]/[version]/[filename])(.[ext])
		artifactory.servers[0].repoAndPatterns[0].patterns[0]= 
		(?<group>.+)[\/](?<artifact>.+)\/(?<version>.+)\/(?<filename>.+)\.(?<ext>.+)
		
		# Matches maven artifacts of the form [org]/[module]/[version]/[module]-[version]([-classifier])(.[ext])
		
		artifactory.servers[0].repoAndPatterns[0].patterns[1]=
		(?<group>.+)/(?<module>[^/]+)/(?<version>[^/]+)/(?<artifact>\\k<module>)-\\k<version>(-(?<classifier[^\\.]+))?(\\.(?<ext>.+))?

		# Matches ivy files of the form [org]/[module]/[revision]/ivy-[revision](-[classifier]).xml 
		
		artifactory.servers[0].repoAndPatterns[0].patterns[2]=
		(?<group>.+)/(?<module>[^/]+)/(?<version>[^/]+)/(?<artifact>ivy)-\\k<version>(-(?<classifier>[^\\.]+))?\\.(?<ext>xml)

		# Matches ivy artifact files of the form [org]/[module]/[revision]/[type]/[artifact]-[revision](-[classifier])			(.[ext])
		
		artifactory.servers[0].repoAndPatterns[0].patterns[3]=
		(?<group>.+)/(?<module>[^/]+)/(?<version>[^/]+)/(?<type>[^/]+)/(?<artifact>[^\\.-/]+)-\\k<version>(-(?<classifier>[^\\.]+))?(\\.(?<ext>.+))?
		
		# Artifactory mode - possible values (ARTIFACT_BASED, REPO_BASED)
		artifactory.mode= ARTIFACT_BASED
		
		# Collector lastUpdated offset value
		artifactory.offSet = 3600000
		

```
