[![Docker Stars](https://img.shields.io/docker/stars/capitalone/hygieia-api.svg)](https://hub.docker.com/r/capitalone/hygieia-api/)
[![Docker Stars](https://img.shields.io/docker/pulls/capitalone/hygieia-api.svg)](https://hub.docker.com/r/capitalone/hygieia-api/)

# Hygieia℠ API

This contains all common REST api services that work with source data system data, which has already
been collected by other service tasks.  This is an abstraction from the local data layer, and the source
system data layer. All REST controllers should be generic to their purpose, and should not be specific
to any given source system.

This project uses Spring Boot to package the api as an executable JAR with dependencies.


## Building

Run `mvn install` to package the collector into an executable JAR file.


## API Properties file

The API layer needs a property file in following format:

```properties
# dashboard.properties
dbname=dashboard
dbusername=[MogoDb Database Username, defaults to empty]
dbpassword=[MongoDb Database Password, defaults to empty]
dbhost=[Host on which MongoDb is running, defaults to localhost]
dbport=[Port on which MongoDb is listening, defaults to 27017]
dbreplicaset=[false if you are not using MongoDB replicaset]
dbhostport=[host1:port1,host2:port2,host3:port3]
server.contextPath=[Web Context path if any]
server.port=[Web server port - default is 8080]
corsEnabled=false
corsWhitelist=http://domain1.com:port,http://domain2.com:port
version.number=@application.version.number@

auth.expirationTime=[JWT expiration time in milliseconds]
auth.secret=[Secret Key used to validate the JWT tokens]

# LDAP Server Url, including port of your LDAP server
auth.ldapServerUrl=[ldap://company.com:389]

# If using standard ldap
# LDAP User Dn Pattern, where the username is replaced with '{0}'
auth.ldapUserDnPattern=[uid={0},OU=Users,dc=your,dc=company,dc=com]

# If using ActiveDirectory
# This will be the domain part of your userPrincipalName
auth.adDomain=[company.com]
# This will be your root dn
auth.adRootDn=[dc=your,dc=company,dc=com]
# This will be your active directory url (required for AD)
auth.adUrl=[Need an example]

monitor.proxy.host=[hostname of proxy server]
monitor.proxy.type=[http|socks|direct]
monitor.proxy.port=[port enabled on proxy server]
monitor.proxy.username=[proxy username]
monitor.proxy.password=[proxy password]
```

All the above values are optional. Even without the property file you must be able to run the api (assuming you have mongodb installed with no authorization).
**Note:** When `dbusername` is not present or the value is empty then it skips the mongodb authorization part.
**Note:** If the expiration time is left blank, the token will can be thought of as permanently expired. This will cause the users to never see any content. While leaving this value blank will not terminate application start up, this is a mandatory field if you wish users to see any content.
**Note:** If the secret is left blank, a random key will be generated.  To allow multiple instances of the API to validate the same JWT token, provide the same key to each instance of the API running.  
**Note:** If both LDAP parameters are not provided, LDAP will not be an available authentication provider.
**Note:** When enabling proxy support for the monitor widget, if the host is not supplied the rest of the monitor.proxy args are ignored. If running with Docker, the port and type will be defaulted to 80 and HTTP. When running locally, these must be supplied.


## Run the API

After you have build your project, from the target folder run the below command,

```bash
java -jar api.jar --spring.config.location=dashboard.properties -Djasypt.encryptor.password=hygieiasecret
```

By default the server starts at port `8080` and uses the context path `/api`.
These values are configurable by using the below 2 properties in `dashboard.properties`.
The jasypt.encryptor.password system property is used to decrypt the database password. For more information, refer to encrypted properties.

```properties
server.contextPath=/api
server.port=8080
```

For more information about the server configuration, see the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

## Docker image


### Create

```bash
# from top-level project
mvn clean package -pl api docker:build
```

### Run

First start Mongodb

For example:
```
docker run -d -p 27017:27017 --name mongodb -v ./mongo:/data/db mongo:latest  mongod --smallfiles
```


Create User:
```
use db
db.createUser({user: "db", pwd: "dbpass", roles: [{role: "readWrite", db: "dashboard"}]})
```
or from CLI:
```bash
mongo 192.168.64.2/admin --eval 'db.getSiblingDB("db").createUser({user: "db", pwd: "dbpass", roles: [{role: "readWrite", db: "dashboard"}]})'
```

More details: <https://hub.docker.com/r/library/mongo/>


Then running the API from docker is easy:

```
docker run -t -p 8080:8080 --link mongodb:mongo -v ./logs:/hygieia/logs -i hygieia-api:latest
```

### Environment variables

Environment variables for dashboard properties can be specified like:

```
docker run -t -p 8080:8080 -v ./logs:/hygieia/logs -e "SPRING_DATA_MONGODB_HOST=127.0.0.1" -i hygieia-api:latest
```

For more properties see the [Dockerfile](Dockerfile)

### List containers

View port by running
```bash
docker ps
```

### API Access

Take the port mapping and the IP for your docker-machine <env> ip and verify by http://<docker-machine env ip>:<docker port for hygieia_api>/api/dashboard

### Troubleshooting
If the api module fails to launch with the following error, then follow the 2 steps listed below to fix the problem
``Error creating bean with name 'dashboardRepository': Invocation of init method failed; nested exception is
org.springframework.dao.DuplicateKeyException: Write failed with error code 11000 and error message 'null'``

Step 1 : save the lines below to a file called fixdups.js
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
Step 2 : run the following on the command line
```bash
mongo <dbhost>:<dbport>/<dbname> fixdups.js
```

## Rundeck Webhook Integration

Hygieia supports registering deployments using the Rundeck [webhook](http://rundeck.org/docs/manual/jobs.html#webhooks).  In the Rundeck job configuration, select **Send Notification?** and check the on success and on failure webhook checkboxes.  Configure the URL as `http://<apihost>:<apiport>/api/deploy/rundeck`.  In order to provide configurability, a few additional features can be added to the webhook URL to assist in locating the proper data to register the deployment.

Additional request parameters can be added to the webhook URL to provide input on where to locate this data.  These parameters can be specified as `optionName=<value>` or `optionNameParam=<value>`.  When the webhook URL provides a parameter in the form `optionName=<value>`, it will use the value provided in the parameter for the field in Hygieia.  When the webhook URL provides a request parameter in the form `optionNameParam=<value>`, the option named `<value>` will be queried and the value of that option in the job will be used to populate that field.  Otherwise, the default values will be used.

This can be done for the following options:

* `appName`
* `envName` (required)
* `artifactName` (required)
* `artifactGroup`
* `artifactVersion`
* `niceName` - Name that appears for the collector in the Hygieia UI.

For a couple examples, to set the `artifactName` based on the `deploymentUnit` option in the Rundeck job, the webhook URL would be `http://<apihost>:<apiport>/api/deploy/rundeck?artifactNameParam=deploymentUnit`.  To set the `envName` to be `QA` every time this job runs, the webhook URL would be `http://<apihost>:<apiport>/api/deploy/rundeck?envName=QA`.

If these values are not provided, it will first query the job to see if it has an option that matches the name of the field.  If not, it will look through the following possibilities:

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

For the required fields, if the methods to locate values have been exhausted, the webhook endpoint will fail and no deployment will be registered.  An exception will appear in the Hygieia API log with the field name that is missing from the job.  If `appName` is not set, it will be set based on the Rundeck project name.

### Encrypted Properties

Properties that are recommended to not be stored in plain text can be encrypted/decrypted using jasypt.
Encrypted properties are enclosed in keyword ENC(), i.e. ENC(thisisanencryptedproperty).
To generate an encrypted property, run
`java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.2/jasypt-1.9.2.jar  org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="dbpass" password=hygieiasecret algorithm=PBEWithMD5AndDES` where dbpass is the property value being encrypted and hygieiasecret is the secret. When starting the collector, this secret has to be passed as a System property via `-Djasypt.encryptor.password=hygieiasecret` in order to decrypt the property.

Via docker, pass as an environment variable `docker run -t -p 8080:8080 -v ./logs:/hygieia/logs -e "SPRING_DATA_MONGODB_HOST=127.0.0.1" -e "JASYPT_ENCRYPTOR_PASSWORD=hygieiasecret" -i hygieia-api:latest`.

For additional information, see jasypt spring boot [documentation](https://github.com/ulisesbocchio/jasypt-spring-boot/blob/master/README.md).

Tip: If using GitLab CI Runner, specify the value for JASYPT_ENCRYPTOR_PASSWORD as a secure variable. Secure variables can be added to a Gitlab project by navigating to Project Settings > Variables > Add Variable. 
A secure variable's value is by default not visible in the build log and can only be configured by an administrator of a project.
