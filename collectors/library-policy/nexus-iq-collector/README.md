# Hygieia Nexus IQ Collector

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

## Building and Deploying

To package the collector into an executable JAR file, run:
```
mvn install
```

Copy this file to your server and launch it using :
```
java -JAR nexus-iq-collector.jar
```

## application.properties

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the Sonar collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

### Sample application.properties file

```properties
# Database Name
dbname=dashboard

# Database HostName - default is localhost
dbhost=10.0.1.1

# Database Port - default is 27017
dbport=27017

# MongoDB replicaset
dbreplicaset=[false if you are not using MongoDB replicaset]
dbhostport=[host1:port1,host2:port2,host3:port3]

# Database Username - default is blank
dbusername=db

# Database Password - default is blank
dbpassword=dbpass

# Collector schedule (required)
nexusiq.cron=0 0/5 * * * *

# Nexus IQ server(s) (required) - Can provide multiple
nexusiq.servers[0]=http://nexusiq.company.com

# Nexus IQ username/password - that has read access to all reports etc.
nexusiq.username=mynexusiquserid
nexusiq.password=mynexusiqpassword


#In case of multiple licsense violations for a given library, consider the most strict violation
nexusiq.selectStricterLicense=true

