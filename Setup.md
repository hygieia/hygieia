<img src="https://pbs.twimg.com/profile_images/461570480298663937/N78Jgl-f_400x400.jpeg" width="150";height="50"/>![Image](/UI/src/assets/images/Hygieia_b.png)
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
              ... {
                ... user: "dashboarduser",
                ... pwd: "1qazxSw2",
                ... roles: [
                  ... {role: "readWrite", db: "dashboarddb"}
                        ... ]
                ... })
                Successfully added user: {
                  "user" : "dashboarduser",
                  "roles" : [
                  {
                    "role" : "readWrite",
                    "db" : "dashboarddb"
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

### Build Docker images

* Build the API Image

```bash
mvn clean package
```

```bash
mvn -pl api docker:build
```

* Build the UI Image

```bash
mvn -pl UI docker:build
```

* Bring up the container images

```bash
docker-compose up -d
```

* Create user in mongo

```bash
mongo 192.168.64.2/admin  --eval 'db.getSiblingDB("dashboard").createUser({user: "db", pwd: "dbpass", roles: [{role: "readWrite", db: "dashboard"}]})'
```

* Make sure everything is restarted _it may fail if the user doesn't exist at start up time_

```bash
docker-compose restart
```

* Get the port for the UI

```bash
docker port hygieia-ui
```

### Start Collectors
* To start individual collector as a background process please run the command in below format
  * On linux platform
```bash
nohup java -jar <collector-name>.jar --spring.config.name=<property file name> & >/dev/null
```
