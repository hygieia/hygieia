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
* MongoDB 2.6+
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
         switched to db dashboarddb
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
* [API](https://github.com/capitalone/Hygieia/tree/master/api)

#### Tool Collectors
* In general all the collectors can be run using the following command
```bash
java -jar <Path to collector-name.jar> --spring.config.name=<prefix for properties> --spring.config.location=<path to properties file location>
```
For each individual collector setup click on the links below

  * **Agile Story Management**
    * [VersionOne](https://github.com/capitalone/Hygieia/tree/master/VersionOneFeatureCollector)
    * [Jira](https://github.com/capitalone/Hygieia/tree/master/JiraFeatureCollector)
  * **Source**
    * [GitHub](https://github.com/capitalone/Hygieia/tree/master/GitHubSourceCodeCollector)
    * [Subversion](https://github.com/capitalone/Hygieia/tree/master/SourceCodeCollector)
  * **Build tools**
    * [Jenkins/Hudson](https://github.com/capitalone/Hygieia/tree/master/BuildCollector)
  * **Code Quality**
    * [Sonar](https://github.com/capitalone/Hygieia/tree/master/CodeQualityCollector)
  * **Deployment**
    * [uDeploy 6.x from IBM](https://github.com/capitalone/Hygieia/tree/master/DeployCollector)

You can pick and choose which collectors are applicable for your DevOps toolset or you can write your own collector and plug it in.

#### UI Layer
Please click on the link below to learn about how to build and run the UI layer
 * [UI](https://github.com/capitalone/Hygieia/tree/master/UI)

### Using Docker images

There are two different ways you can run Hygieia using Docker.

####Building your own Docker containers
If you have already followed the instructions above to configure the API and UI layer then building your own containers would be the best way to run Hygieia using Docker containers.

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

If you have already configured MongoDB on your server and do not need a container for MongoDB then remove the mongodb section in the docker-compose.yml file.

* Bring up the container images

```bash
docker-compose up -d
```

To access the UI for Hygieia you will need to get the port for the UI.
* Get the port for the UI

```bash
docker port hygieia-ui
```

You can access Hygieia through the following link (http://localhost:PORTNUM)

####Downloading Hygieia containers
You can download the API, UI and MongoDB containers from the Docker public registry instead of pulling down the source code.

* Create the following docker compose file. Make sure you specify the location of the MongoDB directory that you want all the databases stored in. You can also change which port you want exposed to access the Hygieia UI. If you want Hygieia running on port 80 then change the port number from 8088 to 80. 

```bash
mongodb:
  image: mongo:latest
  container_name: mongodb
  command: mongod --smallfiles
  ports:
   - "27017:27017"
  volumes:
   - MONGODB_DIR:/data/db:rw
  volume_driver: local
hygieia-api:
  image: capitaloneio/hygieia-api:latest
  container_name: hygieia-api
  ports:
  - "8080:8080"
  volumes:
  - ./logs:/hygieia/logs
  links:
  - mongodb:mongo
  environment:
  - SPRING_DATA_MONGODB_DATABASE=dashboarddb
  - SPRING_DATA_MONGODB_HOST=mongo
  - SPRING_DATA_MONGODB_PORT=27017
  - SPRING_DATA_MONGODB_USERNAME=MONGODB_USERNAME
  - SPRING_DATA_MONGODB_PASSWORD=MONGODB_PASSWORD
hygieia-ui:
  image: capitaloneio/hygieia-ui:latest
  container_name: hygieia-ui
  ports:
  - "8088:80"
  links:
  - hygieia-api
```
Save the file as `docker-compose.yml`.

* Run the Mongo container
```bash
docker run mongo:latest
```

* Create user in mongo

If you do not have the mongo shell installed, refer to the above documentation on how to install MongoDB. Install the package `mongodb-org-shell`.

```bash
$ mongo 127.0.0.1  
MongoDB shell version: 3.0.4
connecting to: test  

> use dashboarddb
switched to db dashboarddb
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
You should now be able to log into MongoDB with those credentials
```bash
mongo 127.0.0.1/dashboarddb -u dashboarduser -p
```

If you are unable to authenticate and login, you may need to enable remote authentication
```bash
$ mongo 127.0.0.1  
MongoDB shell version: 3.0.4
connecting to: test  

>db.auth("dashboarduser", "dashboardpwd")
```

The function returns 1 for success and 0 for failure.

* Shut down the MongoDB container

```bash
docker stop CONTAINER_ID
```

* Start up all the containers 

```bash
docker-compose up -d
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
