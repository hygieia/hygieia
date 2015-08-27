# Hygieia API

This contains all common REST api services that work with source data system data, which has already
been collected by other service tasks.  This is an abstraction from the local data layer, and the source
system data layer. All REST controllers should be generic to their purpose, and should not be specific
to any given source system.

This project uses Spring Boot to package the api as an executable JAR with dependencies.


## Building and Deploying

Run mvn install to package the collector into an executable JAR file. Copy this file to your server and launch it using
java -JAR hudson-collector.jar. You will need to provide an application.properties file that contains information about how
to connect to the Dashboard MongoDB database instance, as well as properties the Hudson collector requires. See
the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files)
for information about sourcing this properties file.


## API Properties file

The API layer needs a property file in following format:

```properties
# dashboard.properties
dbname=dashboarddb
dbusername=[MogoDb Database Username]
dbpassword=[MongoDb Database Password]
dbhost=[Host on which MongoDb is running]
dbport=[Port on which MongoDb is listening]
```

For the API web application to use this property file, make sure that the following environment variable is set in the following format:

```bash
DASHBOARD_PROP=[path to dashboard.properties file]
```


## Run the API

After you have build your project, from the target folder run the below command,

```bash
   export DASHBOARD_PROP=<Path to dashboard.properties file>
   java -jar api.jar --server.port=8080 --server.contextPath=/api 
```

