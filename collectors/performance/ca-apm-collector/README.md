Ca-Apm Collector
=================

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

Building and Deploying
--------------------------------------

Edit pom.xml
Work with the appropriate server for

 <repository>
      <id>nexus</id>
      <url>http://company.com:port/nexus/content/groups/public/</url>
    </repository>
  </repositories>
  

Run mvn install to package the collector into an executable JAR file. Copy this file to your server and launch it using
java -JAR ca-apm-collector-2.0.2-SNAPSHOT.jar. You will need to provide an application.properties file that contains information about how
to connect to the Dashboard MongoDB database instance, as well as properties the UDeploy collector requires. See
the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files)
for information about sourcing this properties file.



###Sample application.properties file
--------------------------------------

# Default server configuration
debug=true
spring.jpa.show-sql: true
# Default server configuration values

#server.contextPath=/api
server.port=${PORT:0}

dbhost=localhost
dbport=27017
dbname=dbname
dbusername=dbuser
dbpassword=dbpass

CaApm.cron= 0 0/1 * * * *

CaApm.alertWsdl=
CaApm.modelWsdl=

CaApm.user=
CaApm.password=