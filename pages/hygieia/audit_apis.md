---
title: Hygieia Audit API
tags:
keywords:
toc: true
summary: Learn how to install and configure Hygieia Audit API
sidebar: hygieia_sidebar
permalink: api-audit.html
---

## Hygieia Audit API

[![Build Status](https://api.travis-ci.com/Hygieia/api-audit.svg?branch=master)](https://travis-ci.com/Hygieia/api-audit?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Hygieia_api-audit&metric=alert_status)](https://sonarcloud.io/dashboard?id=Hygieia_api-audit)
[![Maven Central](https://img.shields.io/maven-central/v/com.capitalone.dashboard/api-audit.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.capitalone.dashboard%22%20AND%20a:%22api-audit%22)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/Hygieia/api-audit.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Hygieia/api-audit/alerts/)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/Hygieia/api-audit.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Hygieia/api-audit/context:java)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Gitter Chat](https://badges.gitter.im/Join%20Chat.svg)](https://www.apache.org/licenses/LICENSE-2.0)
<br>
<br>
Hygieia Audit API - Learn how to install and configure Hygieia audit APIs

Hygieia audit APIs are a collection of API endpoints that serve to audit CI/CD data gathered by Hygieia collectors. The audit API provides endpoints to audit individual widgets on the Dashboard. In addition to these endpoints, Hygieia also provides a dashboard-level audit API. 

The audit API logic adds various audit flags depending on the data. 

For detailed information on audit APIs, see the Swagger documentation available at `http://[your-domain].com/apiaudit/swagger/index.html#`. 

Hygieia uses Spring Boot to package the APIs as an executable JAR file with dependencies.

## Setup Instructions

To configure the Hygieia Audit API layer, execute the following steps:

*	**Step 1 - Artifact Preparation:**

	Please review the two options in Step 1 to find the best fit for you. 
	
	***Option 1 - Download the artifact:***
	
	You can download the SNAPSHOTs from the SNAPSHOT directory [here](https://oss.sonatype.org/content/repositories/snapshots/com/capitalone/dashboard/api-audit/) or from the maven central repository [here](https://search.maven.org/artifact/com.capitalone.dashboard/api-audit).  
	
	***Option 2 - Build locally:***

	To configure the Hygieia API Audit layer, git clone the [api audit repo](https://github.com/Hygieia/api-audit).  Then, execute the following steps:

	To package the Audit API source code into an executable JAR file, run the maven build from the `\api-audit` directory of your source code installation:

	```bash
	mvn install
	```

	The output file `apiaudit.jar` is generated in the `\api-audit\target` folder.

	Once you have chosen an option in Step 1, please proceed: 

*	**Step 2: Set Parameters in the API Properties File**

	Set the configurable parameters in the `dashboard.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the audit API module. To configure the parameters, refer to the [API Audit properties](#api-audit-properties) section.

	For more information about the server configuration, see the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*	**Step 3: Run the API**

	To run the executable file, change directory to 'api-audit\target' and then execute the following command from the command prompt:

	```bash
	java -jar apiaudit.jar --spring.config.location=C:\[path to]\api-audit.properties
	```
	Verify API access from the web browser using the url: http://localhost:8080/apiaudit/ping.

	By default, the server starts at port `8080` and uses the context path `/api-audit`. You can configure these values in the `api-audit.properties` file for the following properties:

	```properties
	server.contextPath=/api-audit
	server.port=8080
	```

	**Note**: The 'jasypt.encryptor.password' system property is used to decrypt the database password. 

## API Audit Properties

The sample `api-audit.properties` lists parameters with sample values to configure the audit API layer. Set the parameters based on your environment setup.

```properties
# api-audit.properties
dbname=dashboarddb
dbusername=dashboarduser[MogoDb Database Username, defaults to empty]
dbpassword=dbpassword[MongoDB Database Password, defaults to empty]
dbhost=[Host on which MongoDB is running, defaults to localhost]
dbport=[Port on which MongoDB is listening, defaults to 27017]
dbreplicaset=[False if you are not using MongoDB replicaset]
dbhostport=[host1:port1,host2:port2,host3:port3]
server.contextPath=[Web Context path, if any]
server.port=[Web server port - default is 8080]
logRequest=false
logSplunkRequest=false
serviceAccountOU=SAOU1,SAOU2 [comma separated list of OU setup in LDAP for whitelist of Service Accounts]

# pattern to match the featureID/storyNumber (Jira ID, VersionOne ID, etc) for traceability
featureIDPattern=((?<!([A-Za-z]{1,10})-?)[A-Z]+-\\d+)
```

All values in the `api-audit.properties` file are optional. If you have MongoDB installed with no authorization, you must be able to run the API even without the properties file.

**Note**: If the value of `dbusername` is empty, then system skips MongoDB authorization.

## Docker Image for API Audit 

You can install Hygieia by using a docker image from docker hub. This section gives detailed instructions on how to download and run with Docker. 

*	**Step 1: Download**

	Navigate to the audit api docker hub location [here](https://hub.docker.com/r/hygieiadoc/apiaudit/tags) and download the latest image (most recent version is preferred).  Tags can also be used, if needed.

*	**Step 2: Run with Docker**

	```Docker run -e SKIP_PROPERTIES_BUILDER=true -v properties_location:/hygieia/config image_name```
	
	- <code>-e SKIP_PROPERTIES_BUILDER=true</code>  <br />
	indicates whether you want to supply a properties file for the java application. If false/omitted, the script will build a properties file with default values
	- <code>-v properties_location:/hygieia/config</code> <br />
	if you want to use your own properties file that located outside of docker container, supply the path here. 
		- Example: <code>-v /Home/User/Document/application.properties:/hygieia/config</code>


## Create a New Audit API

The steps to create a new audit API are as follows:

1. Create a new rest controller or add to an existing controller.
2. Create a new service interface and new service implementation.
3. Add new request and response classes.

**Note**: For common data models used in the audit APIs, refer the core module's model package.
