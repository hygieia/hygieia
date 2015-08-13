<img src="https://pbs.twimg.com/profile_images/461570480298663937/N78Jgl-f_400x400.jpeg" width="150";height="50"/>![Image](/UI/src/assets/images/Hygieia_b.png)
--
### Hygieia Setup Instructions
The following components are required to run Hygieia:

* [UI](https://github.com/capitalone/Hygieia/tree/master/UI)
* [API](https://github.com/capitalone/Hygieia/tree/master/api)
* Mongo DB 2.6+
     * [Download & Installation instructions](https://www.mongodb.org/downloads#previous)
     * Configure Mongodb
      * Name the database as dashboarddb. (Note: This is the same database that the collectors write to. So make sure that this name matches with the database names in collector properties)
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

    You can pick and choose which collectors are applicable for your DevOps toolset or you can write your own collector and plug it in.

    ###How to build the project
    We have included a parent pom for your use, the parent pom will build each of the individual projects in correct order.

    * In the root folder where master pom.xml resides execute below command

   <code> mvn --settings [Path to your settings.xml] clean compile install package </code>
