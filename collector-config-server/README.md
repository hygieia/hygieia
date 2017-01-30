# Hygieia℠ Collector Config Server

This is a spring boot application that acts as a server of property files in a version controlled repository (i.e. application.properties in a git repository). Collectors can talk to this server to get the latest config values. Property file values that often get updated are good candidates to be pushed in a version controlled repo and served up by collector config server. The list of servers that the jenkins collector gather data from is a good example. It's ideal to be able to add, update, or delete servers, without having to change the property file packaged with the jenkins collector, rebuild and restart the collector.


Below is a setup to illustrate the config server to client (collector) relationship:

**collector config server:**
    -- Cloud config server is deployed at http://localhost:8888
    -- Cloud config server is connected to a git repo that has jenkins.properties
    -- jenkins.propeties has the following content

 ```
    jenkins.servers[0]=http://server1.my.company.org/jenkins/
    jenkins.servers[1]=http://server2.my.company.org/jenkins/
 ```

**jenkins collector (aka client):**
    -- specifies a bootstrap.properties file (configuration that will be loaded earlier than any other configuration)
    -- bootstrap.properties has the following content
 ```
    spring.application.name=jenkins
    spring.cloud.config.uri=http://localhost:8888
 ```
-- HusdonSettings.java is the bean where jenkins.servers map to and is annotated with  @RefreshScope 
 ```
    @Component 
    @ConfigurationProperties(prefix = "jenkins")  
    public class HudsonSettings { 	 
    private List<String> servers;  
    ... 
    } 
 ```
 
-- HudsonRefreshConfigService is a spring managed bean and has a scheduled (via @Scheduled) invocation of org.springframework.cloud.endpoint.RefreshEndpoint.refresh() using jenkins.cron 
    
 ```
    @Component public class HudsonRefreshConfigService {
    @Autowired 	private RefreshEndpoint refreshEndpoint; 	

    	@Scheduled(cron = "${jenkins.cron}") 	 
    	public void refreshEndpoint() { 		
    	refreshEndpoint.refresh(); 	
    	}  
	} 
 ```

Given the above setup, on start up of the jenkins collector, it will load the values from jenkins.properties. The spring.application.name 'jenkins' identified jenkins.properties as the configuration for the collector-config-server to send to its client (jenkins collector).

Changes made to jenkins.properties, committed and pushed to git, will be picked up by the jenkins collector in its next run. The HudsonRefreshConfigService refreshes beans in the context that has a @RefreshScope annotation, in this case HudsonSettings.java.

## Building

Run `mvn install` to package the collector into an executable JAR file.


## Collector Config Server Properties file

The Collector Config Server needs a property file in following format:

```properties
# application.properties
server.port=[config server port, (from spring cloud guide) make sure to specify a different server.port value to avoid port conflicts when you run both this server and another Spring Boot application on the same machine.]

spring.cloud.config.server.git.uri=[uri to the git repository of collectors' properties files]
spring.cloud.config.server.git.username=[username to connect to git]
spring.cloud.config.server.git.password=[password to connect to git]

```

The server can connect to other version controlled repositories like svn and even a local file system by prefixing the uri with 'file://'. Refer to spring cloud config [documentation](https://cloud.spring.io/spring-cloud-config/spring-cloud-config.html) for full details.

## Run the collector config server

After you have built your project, from the target folder run

```bash
java -jar collector-config-server.jar 
```

## Docker image

### Create
```bash
# from top-level project
mvn clean package -pl collector-config-server docker:build
```

### Run
```
docker run -t -p 8888:8888 -i hygieia-collector-config-server:latest
```