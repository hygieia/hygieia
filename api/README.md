# Hygieia API

This contains all common REST api services that work with source data system data, which has already
been collected by other service tasks.  This is an abstraction from the local data layer, and the source
system data layer.  All REST controllers should be generic to their purpose, and should not be specific
to any given source system.


# API Properties file

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


# Run the API
 ### 1. Embedded Jetty container

```bash
mvn jetty:run   -DPROP_FILE=<Path to dashboard.properties file>
```

  ### 2. Tomcat container
   * Copy the genrated war file  from project target folder after you have build the project into tomcat webapps folder
   * Add a file setEnv.sh into Tomcat bin folder with following content
   ```bash
   export DASHBOARD_PROP=<Path to dashboard.properties file>
   ```
   * Start the tomcat container.


# To Docker-ize
Utilize sample config dashboard.properties in docker/dashboard.properties
It assumes there's a host called mongo, you can use docker to link them however ensure you use a volumn mounted mongo instance if productionizing.

```bash
mvn clean package docker:build
mvn docker:removeImage -DimageName=hygieia_api

docker run -h mongo -d --name mongo mongo:latest
docker run -h hygieia_api --link mongo:mongo -P -d --name hygieia_api hygieia_api
```

View port by running
```bash
docker ps
```
Take the port mapping and the IP for your docker-machine <env> ip and verify by http://<docker-machine env ip>:<docker port for hygieia_api>/api/dashboard

