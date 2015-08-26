<img src="https://pbs.twimg.com/profile_images/461570480298663937/N78Jgl-f_400x400.jpeg" width="150";height="50"/>![Image](/UI/src/assets/images/Hygieia_b.png)
--
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
* [API](https://github.com/capitalone/Hygieia/tree/master/api)

#### Tool Collectors

* Collectors for following tools are supported currently please click on links below in order to learn about how to build and run these collectors
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
