# NoFearRelease (NFRR) Audit Collector

## Purpose
The NoFearRelease (NFRR) Collector aggregates audit review JSON responses from Hygieia Audit APIs. It sources information from Peer Review, Static Code Analysis, Static Security Analysis, and Performance Audit APIs into a JSON response.
Hygieia uses Spring Boot to package the collector as an executable JAR file with dependencies. 

## Dependencies/Assumptions

### NFRR Collector
It outputs the following JSON schema
Portfolio,Product,Component,Product_Owner,Portfolio_Owner,Audit_Type,Collection_Status,Audit_Status,URL,Reason for Error

### JSON Response Example:
* Audit Type
    * Specified type in the dashboardReview endpoint (see OpenApi docs)
* Collection Status
    * NOT_CONFIGURED - Audit type has not been setup
    * OK - Collector ran without errors
    * NO_DATA - Audit Statuses are missing
    * ERROR - Collector encountered error(s), such as connection time outs, cron or property issues
* Audit Status
    * OK - Audit met the process standard
    * FAIL - Audit not met
    * NA - not application due to audit type not configured and status missing
### API
### Roadmap Items
* Add other Audit APIs to the NFRR Collector, such as Traceability to build out full representation of the Audit API family.

### Setup Instructions

## Fork and Clone the Collector 

Fork and clone the NFRR Audit Collector from the [GitHub repo](https://github.com/Hygieia/hygieia-audit-nfrr-collector). 

To configure the NFRR Audit Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `hygieia-audit-nfrr-collector` directory of your Hygieia source code installation.

For example, in the Mac terminal, run the following command:

```
cd /Users/[username]/hygieia-audit-nfrr-collector
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```
 mvn install
```

The output file `[collector name].jar` is generated in the `hygieia-audit-nfrr-collector/target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the nfrr-audit Collector.

To configure parameters for the nfrr-audit Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `[collector name].jar` file, change directory to `hygieia-audit-nfrr-collector/target`, and then execute the following from the terminal:

```bash
java -jar [collector name].jar --spring.config.name=nfrr-audit-collector --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

```properties
		# Database Name
		dbname=dashboarddb

		# Database HostName - default is localhost
		dbhost=10.0.1.1

		# Database Port - default is 27017
		dbport=27017

		# MongoDB replicaset
		dbreplicaset=[false, if you are not using MongoDB replicaset]
		dbhostport=[host1:port1,host2:port2,host3:port3]

		# Database Username - default is blank
		dbusername=dashboarduser

		# Database Password - default is blank
		dbpassword=dbpassword

		# Collector schedule (required)
		nfrr.cron=0 0/30 * * * *

		# Audit time range - days from present
		nfrr.days=30

		# Collector servers (required) - can be multiple servers
		nfrr.servers[0]=https://nfrr.company.com

		# Collector job depth (required) should be set to at least 1, and more if you use folder jobs, and so on.
		nfrr.jobDepth=4
```
