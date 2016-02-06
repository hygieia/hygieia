:<img src="https://pbs.twimg.com/profile_images/461570480298663937/N78Jgl-f_400x400.jpeg" width="150";height="50"/>![Image](/UI/src/assets/images/Hygieia_b.png)
--

### Build Hygieia
```bash
mvn clean install package
```
The above command will build all components for Hygieia.

### Hygieia Setup Instructions
The following components are required to run Hygieia:

#### Database
* MongoDB 3.0+
     * [Download & Installation instructions](https://www.mongodb.org/downloads#previous)
     * Configure MongoDB
      * Go to the bin directory of your mongodb installation and run the following command to start the mongodb do make sure that data directory should pre-exist at the target location <br/>
       <code>mongod --dbpath < path to the data directory> </code> <br/>
       for e.g <code> /usr/bin/mongodb-linux-x86_64-2.6.3/bin/mongod --dbpath /dev/data/db </code>
      * Run the following commands as shown below at mongodb command prompt
        <code> /usr/bin/mongodb-linux-x86_64-2.6.3/bin/mongo </code>  
        ```Shell
         $ mongo  
         MongoDB shell version: 3.0.4
         connecting to: test  

         > use dashboardb
         switched to db dashboardb
         > db.createUser(
                  {
                    user: "db",
                    pwd: "dbpass",
                    roles: [
                       {role: "readWrite", db: "dashboard"}
                            ]
                    })
                Successfully added user: {
                  "user" : "db",
                  "roles" : [
                  {
                    "role" : "readWrite",
                    "db" : "dashboard"
                  }
                  ]
                }  
                ```


We recommend that you download  MongoDB clients(RoboMongo etc) to connect to your local
running Database and make sure that dashboarddb is created and you are successfully able to connect to it.

#### API Layer
Please click on the link below to learn about how to build and run the API layer
* [API](/api)

#### Tool Collectors
* In general all the collectors can be run using the following command
```bash
java -jar <Path to collector-name.jar> --spring.config.name=<prefix for properties> --spring.config.location=<path to properties file location>
```
For each individual collector setup click on the links below

  * **Agile Story Management**
    * [VersionOne](versionone-feature-collector)
    * [Jira](jira-feature-collector)
  * **Source**
    * [GitHub](github-scm-collector)
    * [Subversion](subversion-scm-collector)
  * **Build tools**
    * [Jenkins/Hudson](jenkins-build-collector)
  * **Code Quality**
    * [Sonar](sonar-codequality-collector)
  * **Deployment**
    * [uDeploy 6.x from IBM](udeploy-deployment-collector)

You can pick and choose which collectors are applicable for your DevOps toolset or you can write your own collector and plug it in.

#### UI Layer
Please click on the link below to learn about how to build and run the UI layer
 * [UI](/UI)

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

Only the above proxy settings (non authentication) may required to be set on your deployment instance.  Additionally, please updated all property files for each collector/API configuration with their specific proxy setting property.

### Build Docker images and setup id for mongodb

* Build the containers

```bash
mvn -pl  api,UI,jenkins-build-collector,github-scm-collector,jenkins-cucumber-test-collector,jira-feature-collector  docker:build
```

* Bring up the container images

```bash
docker-compose up -d
```

* Create user in mongo (if you log into the container then you dont have to install mongo locally)

```bash
docker exec -t -i mongodb2 bash
```
```bash
mongo 192.168.64.2/admin  --eval 'db.getSiblingDB("dashboard").createUser({user: "db", pwd: "dbpass", roles: [{role: "readWrite", db: "dashboard"}]})'
```

### create a docker-compose.override.yml to configure your environment
#### These are the most common entries, the uncommented ones are mandatory if you want the collector to work
##### For dev/testing you will find it useful to change the CRON entries to "0 * * * * *"
```
hygieia-github-scm-collector:
  environment:
#  - GITHUB_HOST=github.com
#  - GITHUB_CRON=0 0/5 * * * *
#  - GITHUB_COMMIT_THRESHOLD_DAYS=15
hygieia-jira-feature-collector:
 environment:
# - DEBUG=1
 - JIRA_BASE_URL=https://company.atlassian.net/
 - JIRA_CREDENTIALS=amltQHN0cmF0ZW5nbGxjLmNvbTp0M0NkZUpaNk00UkZ5SFdSbXMK
 - JIRA_ISSUE_TYPE_ID=10200
 - JIRA_SPRINT_DATA_FIELD_NAME=customfield_10007
 - JIRA_EPIC_FIELD_NAME=customfield_10008
hygieia-jenkins-build-collector:
 environment:
# - DEBUG=1
# - JENKINS_CRON=0 0/5 * * * *
 - JENKINS_MASTER=http://localhost:8080/jenkins
 - JENKINS_USERNAME=username
 - JENKINS_API_KEY=XXXXXXXXXXXXXXXXXXXXXXXXXXX
hygieia-jenkins-cucumber-test-collector:
 environment:
# - DEBUG=1
# - JENKINS_CRON=0 0/5 * * * *
 - JENKINS_MASTER=http://localhost:8080/jenkins
 - JENKINS_USERNAME=username
 - JENKINS_API_KEY=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
 - JENKINS_CUCUMBER_JSON_FILENAME=cucumber-report.json
```

* Make sure everything is restarted _it may fail if the user doesn't exist at start up time_

```bash
docker-compose restart
```

* Get the port for the UI

```bash
docker port hygieia-ui
```

### Start Collectors in the background (optional as they are all running in containers by default)
* To start individual collector as a background process please run the command in below format
  * On linux platform
```bash
nohup java -jar <collector-name>.jar --spring.config.name=<property file name> & >/dev/null
```
