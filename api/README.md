[![Docker Stars](https://img.shields.io/docker/stars/capitalone/hygieia-api.svg)](https://hub.docker.com/r/capitalone/hygieia-api/)
[![Docker Stars](https://img.shields.io/docker/pulls/capitalone/hygieia-api.svg)](https://hub.docker.com/r/capitalone/hygieia-api/)

# Hygieia℠ API

This contains all common REST api services that work with source data system data, which has already
been collected by other service tasks.  This is an abstraction from the local data layer, and the source
system data layer. All REST controllers should be generic to their purpose, and should not be specific
to any given source system.

This project uses Spring Boot to package the api as an executable JAR with dependencies.


## Building

Run `mvn install` to package the collector into an executable JAR file.


## API Properties file

The API layer needs a property file in following format:

```properties
# dashboard.properties
dbname=dashboard
dbusername=[MogoDb Database Username, defaults to empty]
dbpassword=[MongoDb Database Password, defaults to empty]
dbhost=[Host on which MongoDb is running, defaults to localhost]
dbport=[Port on which MongoDb is listening, defaults to 27017]
```

All the above values are optional. Even without the property file you must be able to run the api (assuming you have mongodb installed with no authorization).
**Note:** When `dbusername` is not present or the value is empty then it skips the mongodb authorization part.

## Run the API

After you have build your project, from the target folder run the below command,

```bash
java -jar api.jar --spring.config.location=dashboard.properties
```

By default the server starts at port `8080` and uses the context path `/api`.
These values are configurable by using the below 2 properties in `dashboard.properties`.

```properties
server.contextPath=/api
server.port=8080
```

For more information about the server configuration, see the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

## Docker image


### Create

```bash
# from top-level project
mvn clean package -pl api docker:build
```

### Run

First start Mongodb

For example:
```
docker run -d -p 27017:27017 --name mongodb -v ./mongo:/data/db mongo:latest  mongod --smallfiles
```


Create User:
```
use db
db.createUser({user: "db", pwd: "dbpass", roles: [{role: "readWrite", db: "dashboard"}]})
```
or from CLI:
```bash
mongo 192.168.64.2/admin --eval 'db.getSiblingDB("db").createUser({user: "db", pwd: "dbpass", roles: [{role: "readWrite", db: "dashboard"}]})'
```

More details: <https://hub.docker.com/r/library/mongo/>


Then running the API from docker is easy:

```
docker run -t -p 8080:8080 --link mongodb:mongo -v ./logs:/hygieia/logs -i hygieia-api:latest
```

### Environment variables

Environment variables for dashboard properties can be specified like:

```
docker run -t -p 8080:8080 -v ./logs:/hygieia/logs -e "SPRING_DATA_MONGODB_HOST=127.0.0.1" -i hygieia-api:latest
```

For more properties see the [Dockerfile](Dockerfile)

### List containers

View port by running
```bash
docker ps
```

### API Access

Take the port mapping and the IP for your docker-machine <env> ip and verify by http://<docker-machine env ip>:<docker port for hygieia_api>/api/dashboard
