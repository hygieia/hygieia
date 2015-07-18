<img src="https://pbs.twimg.com/profile_images/461570480298663937/N78Jgl-f_400x400.jpeg" width="150";height="50"/>![Image](/UI/src/assets/images/Hygieia_b.png)
--
### Hygieia Setup Instructions
For Hygieia  you require following components to run

* [UI](https://github.com/capitalone/Hygieia/tree/master/UI)
* [API](https://github.com/capitalone/Hygieia/tree/master/api)
* Mongo DB 2.6+
     * [Download & Installation instructions](https://www.mongodb.org/downloads#previous)
     * Configure Mongodb
      * Name the database as dashboarddb.
      * create a user called dashboarduser with read/write priveleges.
      * Turn Authentication on.


* Collectors for each widget you want data to be collected for.
* Collectors for following tools are supported currently
  * [**Agile Story Management**](https://github.com/capitalone/Hygieia/tree/master/FeatureCollector)
    * VersionOne
    * Jira
  * [**Source**](https://github.com/capitalone/Hygieia/tree/master/SourceCodeCollector)
    * Github
    * Subversion
  * [**Build tools**](https://github.com/capitalone/Hygieia/tree/master/BuildCollector)
    * Jenkins/Hudson
  * [**Code Quality**](https://github.com/capitalone/Hygieia/tree/master/CodeQualityCollector)
    * Sonar
  * [**Deployment**](https://github.com/capitalone/Hygieia/tree/master/DeployCollector)
    * uDeploy 6.x from IBM

    You can pick and choose what collectors are applicable for your Devops toolset, or following the guidance provided you can write your own collector and plug it in.
