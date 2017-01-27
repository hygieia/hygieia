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

# Sonar Metrics
sonar.metrics=ncloc,line_coverage,violations,critical_violations,major_violations,blocker_violations,sqale_index,test_success_density,test_failures,test_errors,tests
```


## boostrap.properties
If you want to take advantage of being able to update property file values without having to restart the collector, leverage the collector-config-server. Properties that often get updated (i.e. sonar.servers) are prime candidates for the collector-config-server. All that needs to be done is source control a sonar.properties file to the repository where collector-config-server is pointing to, then provide values to bootrap.properties:

```
# The name of the property file tracked by the collector-config-server. 
# Specify 'sonar' if property file is sonar.properties
spring.application.name=sonar

# The url of the collector-config-server
spring.cloud.config.uri=http://localhost:8888
```

### Docker
If the collector is a client of the collector-config server make sure to link the collector with collector-config-server and specify the spring.cloud.config.uri as http://[linked collector-config-server container]:[collector-config-server port]
```
docker run -dit --name hygieia-sonar-codequality-collector --link hygieia-api:hygieia-api --link hygieia-collector-servers-config:hygieia-collector-servers-config -e "spring.cloud.config.uri=http://hygieia-collector-servers-config:8888" hygieia-sonar-codequality-collector:latest
```
