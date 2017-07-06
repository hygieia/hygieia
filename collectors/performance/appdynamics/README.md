Appdynamics Collector
=====================

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

Building and Deploying
--------------------------------------

Run
```
mvn install
```
to package the collector into an executable JAR file. Copy this file to your server and launch it using :
```
java -JAR appdynamics-collector.jar --spring.config.name=appdynamics --spring.config.location=<appdynamics.properties location>
```
You will need to provide an **appdynamics.properties** file that contains information about how
to connect to the Dashboard MongoDB database instance, as well as properties the Appdynamics collector requires. See
the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files)
for information about sourcing this properties file.

###Sample application.properties file
--------------------------------------

    #Database Name
    database=dashboard

    #Database HostName - default is localhost
    dbhost=localhost

    #Database Port - default is 27017
    dbport=27017

    #Database Username - default is blank
    dbusername=db

    #Database Password - default is blank
    dbpassword=dbpass

    #Logging File
    logging.file=./logs/appd-collector.log

    #Collector schedule (required)
    appdynamics.cron=1 * * * * *

    #Appdynamics server (required)
    appdynamics.instanceUrl=http://appdynamics.company.com

    #Appdynamics Username (required)
    appdynamics.username=APPD_USERNAME (if multi-tenancy APPD_USERNAME@TENANT)

    #Appdynamics Password (required)
    appdynamics.password=APPD_PASSWORD

    #Appdynamics Dashboard (required)
    appdynamics.dashboardUrl=http://appdynamics.company.com/controller/#/location=APP_DASHBOARD&timeRange=last_15_minutes.BEFORE_NOW.-1.-1.15&application=%s&dashboardMode=force
