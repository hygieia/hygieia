# Hygieia Artifact Collectors / Artifactory

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

## Building and Deploying

To package the collector into an executable JAR file, run:
```
mvn install
```

Copy this file to your server and launch it using :
```
java -jar artifactory-collector.jar
```

## application.properties

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the Artifactory collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

### Sample application.properties file

```properties
# Database Name
dbname=dashboard

# Database HostName - default is localhost
dbhost=192.168.33.11

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
artifactory.cron=0 0/5 * * * *

# Artifactory server (required) - Can provide multiple
artifactory.servers[0]=https://www.jfrog.com/artifactory/

# Artifactory user name (required)
artifactory.username=bobama

# Artifactory api key (required)
artifactory.apiKey=s3cr3t

# The repo to collect artifacts from (required) - Can provide multiple (comma separated for each server) 
artifactory.repos[0]=prerelease,release

# Artifactory REST endpoint
artifactory.endpoint=artifactory/

# Artifact Regex Patterns
# Each artifact found is matched against the following patterns in order (first one wins)
# The following capture groups are available:
#  - group
#  - module
#  - artifact
#  - version
#  - classifier
#  - ext
# Matches maven artifacts of the form [org]/[module]/[version]/[module]-[version]([-classifier])(.[ext])
artifactory.patterns[0]=(?<group>.+)/(?<module>[^/]+)/(?<version>[^/]+)/(?<artifact>\\k<module>)-\\k<version>(-(?<classifier>[^\\.]+))?(\\.(?<ext>.+))?

# Matches ivy files of the form [org]/[module]/[revision]/ivy-[revision](-[classifier]).xml 
artifactory.patterns[1]=(?<group>.+)/(?<module>[^/]+)/(?<version>[^/]+)/(?<artifact>ivy)-\\k<version>(-(?<classifier>[^\\.]+))?\\.(?<ext>xml)

# Matches ivy artifact files of the form [org]/[module]/[revision]/[type]/[artifact]-[revision](-[classifier])(.[ext])
artifactory.patterns[2]=(?<group>.+)/(?<module>[^/]+)/(?<version>[^/]+)/(?<type>[^/]+)/(?<artifact>[^\\.-/]+)-\\k<version>(-(?<classifier>[^\\.]+))?(\\.(?<ext>.+))?

```
