![Image](/UI/src/assets/img/hygieia_b.png)
--

### Build Hygieia℠
Needs Java 1.8.

```bash
mvn clean install package
```
The above command will build all components for Hygieia.

### Hygieia℠ Setup Instructions
The following components are required to run Hygieia℠:

#### Database
* MongoDB 3.0+
     * [Download & Installation instructions](https://www.mongodb.org/downloads#previous)
     * Configure MongoDB
      * Go to the bin directory of your mongodb installation and run the following command to start the mongodb do make sure that data directory should pre-exist at the target location <br/>
       <code>mongod --dbpath < path to the data directory> </code> <br/>
       for e.g <code> /usr/bin/mongodb-linux-x86_64-2.6.3/bin/mongod --dbpath /dev/data/db </code>
      * Run the following commands as shown below at mongodb command prompt
        <code> /usr/bin/mongodb-linux-x86_64-2.6.3/bin/mongo </code>
          
 ```   
         $ mongo  
         MongoDB shell version: 3.0.4
         connecting to: test  
         
         > use dashboarddb
         switched to db dashboarddb
         > db.createUser(
                  {
                    user: "dashboarduser",
                    pwd: "dbpassword",
                    roles: [
                       {role: "readWrite", db: "dashboard"}
                            ]
                    })
          
         
                    Output similar to below should be seen in your mongo shell
                    
                Successfully added user: {
                  "user" : "dashboarduser",
                  "roles" : [
                  {
                    "role" : "readWrite",
                    "db" : "dashboard"
                  }
                  ]
                }  
```

We recommend that you download MongoDB clients (RoboMongo etc) to connect to your locally
running database and make sure that database: dashboard is created, and you are able to connect to it.


##### Executing via a script
To execute the above via a script in an automated fashion, use `mongosrc.js`:

```bash
  mongo < mongosrc.js
```

#### API Layer
Please click on the link below to learn about how to build and run the API layer
* [API](/api)

#### Tool Collectors
In general, all the collectors can be run using the following command
```bash
java -jar <Path to collector-name.jar> --spring.config.name=<prefix for properties> --spring.config.location=<path to properties file location>
```

You can view the collector inventory [here](collectors/README.md).

You can pick and choose which collectors are applicable for your DevOps toolset or you can write your own collector and plug it in.

#### UI Layer
Please click on the link below to learn about how to build and run the UI layer
 * [UI](/UI)

#### Plugin / Webhook
You can use Jenkins - Hygieia plugin to publish data from Jenkins to Hygieia. Currently, you can publish build, artifact info, sonar results, deployment results and Cucumber test results. You may not need to run corresponding collectors if you use Jenkins for build, deploy, sonar analysis and running cucumber tests.
* [Hygieia Jenkins Plugin](/hygieia-jenkins-plugin)

You can use GitHub webhooks to publish commit information to Hygieia. If you use webhooks, you will not need to run the github collector.
* Your Github webhook's payload url should be set to: http://hygieia-base-url/api/commit/github/v3
* Select to publish just the "push" events


### Configure Proxy

Hygieia supports proxy authentication for working behind corporate firewalls.  For development, please refer to the following configuration differences; for deployment/operations, please refer to the subsequent sub-section:

##### Proxy Config: Developer

Update your Maven settings.xml file:

```
...
<proxies>
       ...
       <proxy>
               <id>your-proxy-id</id>
               <active>true</active>
               <protocol>http</protocol>
               <host>your.proxy.domain.name</host>
               <port>8080</port>
               <!-- For authenticated proxy, please set the following, as well -->
               <username>companyId999</username>
               <password>yourPassword</password>
               <nonProxyHosts>*.local</nonProxyHosts>
       </proxy>
       ...
 </proxies>
...
```

Additionally, set the following export variables:

```bash
export HTTP_PROXY=http://companyId999:yourPassword@your.proxy.domain.name:8080
export HTTPS_PROXY=http://companyId999:yourPassword@your.proxy.domain.name:8080
export JAVA_OPTS="$JAVA_OPTS -Dhttp.proxyHost=your.proxy.domain.name -Dhttp.proxyPort=8080 -Dhttp.proxyUser=companyId999 -Dhttp.proxyPassword=yourPassword"
# This option may be duplicative if you have already updated your
# Maven settings.xml file, but will only help:
export MAVEN_OPTS="$MAVEN_OPTS -Dhttp.proxyHost=your.proxy.domain.name -Dhttp.proxyPort=8080 -Dhttp.proxyUser=companyId999 -Dhttp.proxyPassword=yourPassword"
```

Tests should now run/pass when built from behind a corporate proxy, even if it is an authenticated proxy

##### Proxy Config: Deployment / Operations

Only the above proxy settings (non-authentication) may required to be set on your deployment instance.  Additionally, please update all property files for each collector/API configuration with their specific proxy setting property.

### Build Docker images and setup id for mongodb

* Build the containers

```bash
mvn docker:build
```

* Bring up the container images

```bash
docker-compose up -d
```

* Create a user in Mongo (if you log into the container then you don't have to install Mongo locally)

```bash
docker exec -t -i mongodb2 bash
```
```bash
mongo 192.168.64.2/admin  --eval 'db.getSiblingDB("dashboard").createUser({user: "db", pwd: "dbpass", roles: [{role: "readWrite", db: "dashboard"}]})'
```

## Create a `docker-compose.override.yml` to configure your environment
These are the most common entries, the uncommented ones are mandatory if you want the collector to work.
For dev/testing you will find it useful to change the CRON entries to ``"0 * * * * *"``
```
hygieia-github-scm-collector:
  environment:
  - GITHUB_HOST=github.com
  - GITHUB_CRON=0 * * * * *
  - GITHUB_COMMIT_THRESHOLD_DAYS=300
hygieia-jira-feature-collector:
  environment:
  - JIRA_BASE_URL=https://mycompany.atlassian.net/
  - JIRA_CREDENTIALS=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
  - JIRA_ISSUE_TYPE_ID=10200
  - JIRA_SPRINT_DATA_FIELD_NAME=customfield_10007
  - JIRA_EPIC_FIELD_NAME=customfield_10008
hygieia-jenkins-build-collector:
  environment:
  - JENKINS_CRON=0 * * * * *
  - JENKINS_MASTER=http://192.168.99.100:9100
  - JENKINS_USERNAME=XXXXXXXXXXXXXXXXXXXXXX
  - JENKINS_API_KEY=XXXXXXXXXXXXXXXXXXXXXXXXXX
hygieia-jenkins-cucumber-test-collector:
  environment:
  - JENKINS_CRON=0 * * * * *
  - JENKINS_MASTER=http://192.168.99.100:9100
  - JENKINS_USERNAME=XXXXXXXXXXXXXXXXXXXXXX
  - JENKINS_API_KEY=XXXXXXXXXXXXXXXXX
  - JENKINS_CUCUMBER_JSON_FILENAME=cucumber-report.json
hygieia-sonar-codequality-collector:
  environment:
  - SONAR_URL=http://192.168.99.100:9000
  - SONAR_CRON=0 * * * * *
```

* Make sure everything is restarted - _it may fail if the user doesn't exist at start-up_

```bash
docker-compose restart
```

* Get the port for the UI

```bash
docker port hygieia-ui
```

## How to set up test data
### 1. Set up Git - by configuring it to point to the Github master branch for Hygieia
	a. In the SCM panel, select _git_
	b. Enter the URL: `https://github.com/capitalone/Hygieia.git`
	c. Set the branch to `master`
	ote: For this to work you will need to have set your credentials on the ID that the collectors is running under, the best way to do this is first clone the repo to set your credentials.

### 2. Setup Sonar -  by running a test instance of sonar
	a. `docker-compose -f test-servers/sonar/sonar.yml up -d`
	b. Fill it with data from the Hygieia project
```bash
mvn sonar:sonar -Dsonar.host.url=http://$(docker-machine ip default):9000 -Dsonar.jdbc.url="jdbc:h2:tcp://$(docker-machine ip default)/sonar"
```
	c. You can now go in and configure the quality panel in the UI.

### 3. Set up Jenkins w/cucumber output - by starting a test jenkins master
	a. `docker-compose -f test-servers/jenkins/jenkins.yml up -d`
	b. Run the job: http://192.168.99.100:9100/job/Hygieia_Example_Job/build
	c. Configure the Jenkins Build and Jenkins Cucumber panels using this jobs output.


## Start Collectors in the background (optional as they are all running in containers by default)
* To start individual collector as background processes, use this format:
  * On linux platform
```bash
nohup java -jar <collector-name>.jar --spring.config.name=<property file name> & >/dev/null
```
