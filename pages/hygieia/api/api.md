---
title: All About Hygieia API
tags:
keywords:
summary: Learn how to install and configure Hygieia API
sidebar: hygieia_sidebar
permalink: api.html
---
[![Docker Stars](https://img.shields.io/docker/stars/capitalone/hygieia-api.svg)](https://hub.docker.com/r/capitalone/hygieia-api/)
[![Docker Stars](https://img.shields.io/docker/pulls/capitalone/hygieia-api.svg)](https://hub.docker.com/r/capitalone/hygieia-api/)

Hygieia API layer contains all common REST API services that work with the source system data (collected by service tasks). The Hygieia API layer is an abstraction of the local and source system data layer. All REST controllers should be generic to their purpose, and should not be specific to any given source system.

For detailed information on APIs, see the Swagger documentation.

Hygieia uses Spring Boot to package the API as an executable JAR file with dependencies.

## Setup Instructions

If you do not already have Hygieia installed, you can download or clone Hygieia from the [GitHub repo](https://github.com/capitalone/Hygieia). For information on cloning a repository, see [GitHub Documentation](https://help.github.com/articles/cloning-a-repository/).

To configure the Hygieia API layer, execute the following steps:

*	**Step 1: Run Maven Build**

	To package the API source code into an executable JAR file, run the maven build from the `\Hygieia` directory of your source code installation:

	```bash
	mvn install
	```

	The output file `api.jar` is generated in the `\api\target` folder.

*	**Step 2: Set Parameters in the API Properties File**

	Set the configurable parameters in the `dashboard.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the API module. To configure the parameters, refer to the [API properties](#api-properties-file) file.

	For more information about the server configuration, see the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*	**Step 3: Run the API**

	To run the executable file, change directory to 'api\target' and then execute the following command from the command prompt:

	```bash
	java -jar api.jar --spring.config.location=C:\[path to]\Hygieia\api\dashboard.properties -Djasypt.encryptor.password=hygieiasecret
	```

	Verify API access from the web browser using the url: http://localhost:8080/api/ping. 

	By default, the server starts at port `8080` and uses the context path `\api`. You can configure these values in the `dashboard.properties` file for the following properties:

	```properties
	server.contextPath=/api
	server.port=8080
	```
	**Note**: The 'jasypt.encryptor.password' system property is used to decrypt the database password. For more information, refer to [Encrypted Properties](#encrypted-properties).

## API Properties File

The sample `dashboard.properties` file lists parameters with sample values to configure the API layer. Set the parameters based on your environment setup.

```properties
# dashboard.properties
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

# This will be page size for pagination on Hygieia landing page.if this property is not set, default value is set to 10.
pageSize=[Integer value]
```

All the above values are optional. If you have MongoDB installed with no authorization, you must be able to run the API even without the properties file.

Note the following:

 * If the value of `dbusername` is empty, then system skips MongoDB authorization.
 * Expiration time is mandatory for users to see any content. If you do not specify the expiration time, then the token can be considered as permanently expired. Leaving this value blank will not terminate the application start up. However, users will not be able to see any content 
 * If the secret is left blank, a random key is generated. To allow multiple instances of the API to validate the same JWT token, provide the same key to each running instance of the API.  
 * If both LDAP parameters are not provided, then LDAP is not available as an authentication provider.
 * When enabling proxy support for the monitor widget, if the host is not supplied, the rest of the monitor.proxy args are ignored. If running with Docker, then by default, the port and type is 80 and HTTP respectively. When running locally, you must specify the values for these properties.

## Docker Image for API

You can install Hygieia by creating a Docker image. This section gives detailed instructions to create a Docker image for the API layer. 

For instructions on installing all components Hygieia, see [Build Docker](../Build/builddocker.md).

To create a Docker image for Hygieia's API layer, execute the following steps:

*	**Step 1: Run Maven Build**

	To package the API source code into an executable JAR file, run the maven build from the `\Hygieia` directory of your source code installation:

	```bash
	mvn clean package -pl api docker:build
	```
*	**Step 2: Start MongoDB Docker Container**

	Execute the following commands to start MongoDB, switch to db dashbaord, and then add dashboard user:

	``` bash
	docker run -d -p 27017:27017 --name mongodb -v ./mongo:/data/db mongo:latest  mongod --smallfiles

	# Connect to MongoDB
	docker exec -t -i mongodb bash

	# Switch to db dashbaord
	use dashboarddb

	# Create dashboard user
	db.createUser({user: "dashoarduser", pwd: "dbpassword", roles: [{role: "readWrite", db: "dashboarddb"}]})

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

### Secure APIs Basic Authentication

1. From the admin menu, generate an 'apiToken' for an 'apiUser'.

2. Create a POST request with the following two headers and make a rest call for secured API.

   - Add Authorization header
	
	```properties
	String passwordIsAuthToken = "PasswordIsAuthToken:{\"apiKey\":\"" + <generated apitoken> + "\"}";
	byte[] encodedAuth = Base64.encodeBase64(passwordIsAuthToken.getBytes(StandardCharsets.US_ASCII));
	String authHeader = "apiToken " + new String(encodedAuth);
	Authorization: apiToken <authHeader>
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
* `niceName` - Name that appears for the collector in the Hygieia UI.

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

For the required fields, if the methods to locate values is exhausted, the webhook endpoint fails and deployment will not be registered. An exception appears in the Hygieia API log with the field name that is missing from the job. If `appName` is not set, it will be set based on the Rundeck project name.

### Encrypted Properties

Properties that are recommended not to be stored in plain text can be encrypted/decrypted using jasypt.Encrypted properties are enclosed in keyword ENC(), that is, ENC(thisisanencryptedproperty).

To generate an encrypted property, run the following command:

```
java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.2/jasypt-1.9.2.jar  org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="dbpassword" password=hygieiasecret algorithm=PBEWithMD5AndDES
```
where,

dbpassword - Property value being encrypted, and 
hygieiasecret - the secret. 

When you run the API, this secret has to be passed as a system property using `-Djasypt.encryptor.password=hygieiasecret` in order to decrypt the property.

When using docker, pass the environment variable `docker run -t -p 8080:8080 -v ./logs:/hygieia/logs -e "SPRING_DATA_MONGODB_HOST=127.0.0.1" -e "JASYPT_ENCRYPTOR_PASSWORD=hygieiasecret" -i hygieia-api:latest`.

For additional information, see jasypt spring boot [documentation](https://github.com/ulisesbocchio/jasypt-spring-boot/blob/master/README.md).

**Tip**: When using GitLab CI Runner, specify the value for JASYPT_ENCRYPTOR_PASSWORD as a secure variable. To add secure variables to a Gitlab project, navigate to Project Settings > Variables > Add Variable. 

By default, a secure variable's value is not visible in the build log and can only be configured by a project administrator.

### Troubleshooting Instructions

**Scenario 1**

The API module fails to launch with the following error:

```
Error creating bean with name 'dashboardRepository': Invocation of init method failed; nested exception is
org.springframework.dao.DuplicateKeyException: Write failed with error code 11000 and error message 'null'
```
In this case, execute the following steps:

* **Step 1** : Save the following lines to a file called fixdups.js:

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

* **Step 2** : Run the following in the command line:

```bash
mongo <dbhost>:<dbport>/<dbname> fixdups.js
```

**Scenario 2**

The Hygieia dashboard does not show up for a specific login type you created, before introducing Auth type as 'STANDARD' or 'LDAP'.

In this case, execute the following steps:

**Step 1** : Save the following lines to a file called fixAuths.js 

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

**Step 2** : Run the following in command line.

```bash
mongo <dbhost>:<dbport>/<dbname> fixAuths.js
```

