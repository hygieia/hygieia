---
title: All About Hygieia API
tags:
keywords:
toc: true
summary: Learn how to install and configure Hygieia API
sidebar: hygieia_sidebar
permalink: api.html
---

[![Docker Stars](https://img.shields.io/docker/stars/capitalone/hygieia-api.svg)](https://hub.docker.com/r/capitalone/hygieia-api/)
[![Docker Stars](https://img.shields.io/docker/pulls/capitalone/hygieia-api.svg)](https://hub.docker.com/r/capitalone/hygieia-api/)

Hygieia API layer contains all the typical REST API services that work with the source system data (collected by service tasks). The Hygieia API layer is an abstraction of the local and source system data layer. All API REST controllers are generic to their purpose - they are not specific to any given source system.

For detailed information on APIs, see the Swagger documentation available at `http://[your-domain].com/api/swagger/index.html#`.

Hygieia uses Spring Boot to package the API as an executable JAR file with dependencies.

## Setup Instructions

To configure the Hygieia API layer, first fork and clone the [api repo](https://github.com/Hygieia/api).  Then, execute the following steps:

*	**Step 1: Run Maven Build**

	To package the API source code into an executable JAR file, run the Maven build from the `\api` directory of your source code installation:

	```bash
	mvn install
	```

	The output file `api.jar` is generated in the `\api\target` folder.

*	**Step 2: Set Parameters in the API Properties File**

	Set the configurable parameters in the `api.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the API module. To configure the parameters, refer to the [API properties](#api-properties-file) file.

	For more information about the server configuration, see the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*	**Step 3: Run the API**

	To run the executable file, change directory to 'api\target' and then execute the following command from the command prompt:

	```bash
	java -jar api.jar --spring.config.location=C:\[path to api.properties file] -Djasypt.encryptor.password=hygieiasecret
	```

	Verify API access from the web browser using the url: http://localhost:8080/api/ping.

	By default, the server starts at port `8080` and uses the context path `\api`. You can configure these values in the `api.properties` file for the following properties:

	```properties
	server.contextPath=/api
	server.port=8080
	```
	**Note**: The 'jasypt.encryptor.password' system property is used to decrypt the database password. For more information, refer to [Encrypted Properties](../collectors/collectors.md#encrypted-properties).

## API Properties File

The sample `api.properties` file lists parameters with sample values to configure the API layer. Set the parameters based on your environment setup.

```properties
# api.properties
dbname=dashboarddb
dbusername=dashboarduser[MogoDB Database Username, defaults to empty]
dbpassword=dbpassword[MongoDB Database Password, defaults to empty]
dbhost=[Host on which MongoDB is running, defaults to localhost]
dbport=[Port on which MongoDB is listening, defaults to 27017]
dbreplicaset=[false if you are not using MongoDB replicaset]
dbhostport=[host1:port1,host2:port2,host3:port3]
server.contextPath=[Web Context path, if any]
server.port=[Web server port - default is 8080]
logRequest=false
logSplunkRequest=false
corsEnabled=false
corsWhitelist=http://domain1.com:port,http://domain2.com:port
version.number=@application.version.number@

auth.expirationTime=[JWT expiration time in milliseconds]
auth.secret=[Secret Key used to validate the JWT tokens]
auth.authenticationProviders=[Authentication types you would like to enable, defaults to STANDARD, ex: STANDARD,LDAP]
auth.ldapServerUrl=[LDAP Server URL, including port of your LDAP server]
auth.ldapUserDnPattern=[LDAP User Dn Pattern, where the username is replaced with '{0}']

# LDAP Server URL, including port of your LDAP server
auth.ldapServerUrl=[ldap://company.com:389]

# If using standard LDAP
# LDAP User Dn Pattern, where the username is replaced with '{0}'
auth.ldapUserDnPattern=[uid={0},OU=Users,dc=your,dc=company,dc=com]

# If using ActiveDirectory
# This will be the domain part of your userPrincipalName
auth.adDomain=[company.com]
# This will be your root dn
auth.adRootDn=[dc=your,dc=company,dc=com]
# This will be your active directory URL (required for AD)
auth.adUrl=[Need an example]

monitor.proxy.host=[hostname of proxy server]
monitor.proxy.type=[http|socks|direct]
monitor.proxy.port=[port enabled on proxy server]
monitor.proxy.username=[proxy username]
monitor.proxy.password=[proxy password]

# This will be the page size for pagination on Hygieia landing page. If this property is not set, the default value is set to 10.
pageSize=[Integer value]

# API token generated for basic authentication to secure APIs.
key=[api token]

# SSO properties with header values from UI layer
auth.userEid=[name of the header containing EID]
auth.userEmail=[name of the header containing user's email]
auth.userFirstName=[name of the header containing user's first name]
auth.userLastName=[name of the header containing user's last name]
auth.userMiddelInitials=[name of the header containing user's middle name]
auth.userDisplayName=[name of the header containing user's display name]

# Github sync api settings 

# List of not built commits
githubSyncSettings.notBuiltCommits;

# Maximum history of days to sync from current time. Default to 60 days
githubSyncSettings.firstRunHistoryDays;

# Offset time from last updated // 10 mins default
githubSyncSettings.offsetMinutes;

# Total fetch count // Default to 100
githubSyncSettings.fetchCount;

# Commits and pull sync time // Default to 86400000ms - 1 day in milliseconds
githubSyncSettings.commitPullSyncTime;

```

All values in the `api.properties` file are optional. For instance, if you have MongoDB installed with no authorization, you must be able to run the API even without the properties file.

Note the following:

 * in the `api.properties` file, if you do not define the value of `dbusername`, then Hygieia skips the MongoDB authorization process.
 * Specify the **Expiration Time** to see content on your dashboard. If you do not specify a value for **Expiration Time**, then the token permanently expires, and users will not be able to see any content on the dashboard. However, the application start-up is not impacted by this action.
 * If you do not enter a value for the secret password, then the system generates a random key. To allow multiple instances of the API to validate the same JWT token, provide the same key for each running instance of the API.
 * If you do not provide values for the LDAP parameters, then LDAP is not available as an authentication provider for your application.
 * When enabling proxy support for the monitor widget, if you do not specify, then system ignores the rest of the monitor.proxy arguments. If you are using Docker, then by default, the port is 80 and the type is HTTP. When you run the application locally, you must specify the values for these properties (i.e., for the port and the type).

## Docker Image for API

You can install Hygieia by creating a Docker image. This section gives detailed instructions to create a Docker image for the API layer. 

For instructions on installing all components of Hygieia, see [Build Docker](../Build/builddocker.md).

To create a Docker image for Hygieia's API layer, execute the following steps:

*	**Step 1: Run Maven Build**

	To package the API source code into an executable JAR file, run the maven build from the `\Hygieia` directory of your source code installation:

	```bash
	mvn clean package -pl api docker:build
	```
*	**Step 2: Start MongoDB Docker Container**

	Execute the following commands to start MongoDB, switch to dashbaord with name `dashboarddb`, and then add dashboard user:

	``` bash
	docker run -d -p 27017:27017 --name mongodb -v ./mongo:/data/db mongo:latest  mongod --smallfiles

	# Connect to MongoDB
	docker exec -t -i mongodb bash

	# Switch to db dashbaord
	use dashboarddb

	# Create dashboard user
	db.createUser({user: "dashboarduser", pwd: "dbpassword", roles: [{role: "readWrite", db: "dashboarddb"}]})

	# To execute from CLI:

	mongo 192.168.64.2/admin --eval 'db.getSiblingDB("dashboarddb").createUser({user: "dashboarduser", pwd: "dbpassword", roles: [{role: "readWrite", db: "dashboarddb"}]})'
	```

	For more information on creating docker image for MongoDB, refer to the [Docker Hub Document](https://hub.docker.com/r/library/mongo/).

*   **Step 3: Set Environment Variables**

	Specify the Environment Variables for dashboard properties:

	```
	docker run -t -p 8080:8080 -v ./logs:/hygieia/logs -e "SPRING_DATA_MONGODB_HOST=127.0.0.1" -i hygieia-api:latest
	```

	To define more properties, refer to the [Dockerfile](https://github.com/capitalone/Hygieia/blob/master/api/docker/Dockerfile).

*	**Step 4: Run the API**

	To run the API from Docker, execute the following command from the command prompt:

	```
	docker run -t -p 8080:8080 --link mongodb:mongo -v ./logs:/hygieia/logs -i hygieia-api:latest
	```
	To verify API access from the web browser, take the port mapping and the IP for your docker-machine <env> ip and then verify using url: `http://<docker-machine env ip>:<docker port for hygieia_api>/api/dashboard`

	To list the running containers in the local repository, execute the following command:

	```bash
	docker ps
	```

### Basic Authentication for Secure APIs

To carry out basic authentication for secure APIs, execute the following steps:

1. From the admin menu, generate an 'apiToken' for an 'apiUser'.

2. Create a POST request with the following two headers and make a REST call for secured API.

   - Add Authorization header
	
	```properties
	String passwordIsAuthToken = "PasswordIsAuthToken:{\"apiKey\":\"" + <generated apitoken> + "\"}";
	byte[] encodedAuth = Base64.encodeBase64(passwordIsAuthToken.getBytes(StandardCharsets.US_ASCII));
	String authHeader = "apiToken " + new String(encodedAuth);
	Authorization: <authHeader>
	```

   - Add apiUser header
	
	```
	apiUser <apiuser>
	```

## Rundeck Webhook Integration

Hygieia supports registering deployments using the Rundeck [webhook](http://rundeck.org/docs/manual/jobs.html#webhooks). In the Rundeck job configuration, select **Send Notification?** and check the **on success** and **on failure** webhook checkboxes. Configure the URL as `http://<apihost>:<apiport>/api/deploy/rundeck`. To provide configurability, a few additional features can be added to the webhook URL to locate the proper data for registering the deployment.

You can add additional request parameters to the webhook URL to provide input on locating this data. You can specify these parameters as `optionName=<value>` or `optionNameParam=<value>`. When the webhook URL provides a parameter in the form `optionName=<value>`, it will use the value provided in the parameter for the field in Hygieia. When the webhook URL provides a request parameter in the form `optionNameParam=<value>`, the option named `<value>` is queried and the value of that option in the job is used to populate that field. Otherwise, the default values are used.

You can add additional request parameters for the following options:

* `appName`
* `envName` (required)
* `artifactName` (required)
* `artifactGroup`
* `artifactVersion`
* `niceName` - Name that appears for the collector in Hygieia UI.

For example, to set the `artifactName` based on the `deploymentUnit` option in the Rundeck job, the webhook URL is:
`http://<apihost>:<apiport>/api/deploy/rundeck?artifactNameParam=deploymentUnit`. 
To set the `envName` to be `QA` every time this job runs, the webhook URL is:
`http://<apihost>:<apiport>/api/deploy/rundeck?envName=QA`.

If these values are not provided, the webhook first queries the job to see if it has an option that matches the name of the field. If not, it will look through the following possibilities:

* `appName`
    * `hygieiaAppName`
* `envName`
    * `environment`
    * `env`
    * `hygieiaEnvName`
* `artifactName`
    * `artifactId`
    * `hygieiaArtifactName`
* `artfactGroup`
    * `group`
    * `hygieiaArtifactGroup`
* `niceName`
    * `hygieiaNiceName`

For the required fields, if the methods to locate values is exhausted, the webhook endpoint fails and deployment is not registered. An exception appears in the Hygieia API log with the field name that is missing from the job. If `appName` is not set, it is set based on the Rundeck project name.

### Troubleshooting Instructions

**Scenario 1**

The API module fails to launch with the following error:

```
Error creating bean with name 'dashboardRepository': Invocation of init method failed; nested exception is org.springframework.dao.DuplicateKeyException: Write failed with error code 11000 and error message 'null'
```
In this case, execute the following steps:

*	**Step 1** : Save the following lines to a file called fixdups.js:

	```
	var dupsExist = false;
	db.dashboards.aggregate([
	  { $group: {
		_id: { firstField: "$title"},
		uniqueIds: { $addToSet: "$_id" },
		count: { $sum: 1 }
	  }},
	  { $match: {
		count: { $gt: 1 }
	  }}
	]).forEach(
		function(myDoc) {
			dupsExist = true;
			print(myDoc.count + " dashboards have the same title " + myDoc._id.firstField);
			var arr = myDoc.uniqueIds;
			for(i=0; i<arr.length; i++) {
				var oneDash = db.dashboards.findOne({_id: arr[i]});
				var newTitle = oneDash.title + "_" + oneDash.owner + "_" + i;
				print("Rename " + oneDash.title + " to " + newTitle);
				db.dashboards.update({_id: arr[i]},{$set:{title: newTitle}});
			}
		}
	);

	if (!dupsExist) {
		print("No duplicate title dashboards found.");
	}
	```

*	**Step 2** : Run the following in the command line:

	```bash
	mongo <dbhost>:<dbport>/<dbname> fixdups.js
	```

**Scenario 2**

The Hygieia dashboard does not show up for a specific login type you created, before introducing Auth type as 'STANDARD' or 'LDAP'.

In this case, execute the following steps:

*	**Step 1** : Save the following lines to a file called fixAuths.js 

	```
	var count = 0;
	db.dashboards.aggregate([{$match:{"owners.authType": {$exists : false}}}]).forEach(
		function(myDoc) {
			var ownerName = myDoc.owner;
			print("Updating owner information for dashboard title --"+ myDoc.title+ "  owner name --"+myDoc.owner);
			db.dashboards.update(
				{ _id: myDoc._id},
				{
					$push: {
						owners: {
							$each: [{username: ownerName, authType: "STANDARD"}]
						}
					}
				}
			)
			db.dashboards.update({_id: myDoc._id},{$unset: {owner:1}},{multi: true});
			count++;
		}
	);
	print(count+" dashboards updated successfully");
	```

*	**Step 2** : Run the following in command line:

	```bash
	mongo <dbhost>:<dbport>/<dbname> fixAuths.js
	```

