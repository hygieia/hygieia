# Hygieia Build Collectors / Sonar

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

## Building and Deploying

To package the collector into an executable JAR file, run:
```
mvn install
```

Copy this file to your server and launch it using :
```
java -JAR sonar-collector.jar
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
sonar.cron=0 0/5 * * * *

# Sonar server(s) (required) - Can provide multiple
sonar.servers[0]=http://sonar.company.com
# Sonar version, match array index to the server. If not set, will default to version prior to 6.
sonar.versions[0]=6.31
# Sonar Metrics - Required. 
#Sonar versions lesser than 5.4
sonar.metrics[0]=ncloc,line_coverage,violations,critical_violations,major_violations,blocker_violations,violations_density,sqale_index,test_success_density,test_failures,test_errors,tests
#for Sonar 5.4 and above
sonar.metrics[0]=ncloc,violations,new_vulnerabilities,critical_violations,major_violations,blocker_violations,tests,test_success_density,test_errors,test_failures,coverage,line_coverage,sqale_index,alert_status,quality_gate_details

```
