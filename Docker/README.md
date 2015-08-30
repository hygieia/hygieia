### Dockerizing Hygieia

## Introduction
To be able to install Hygieia in Docker I started out with a clean Ubuntu 14.04 image. Currently the only packages really needed to install Hygieia are

- maven
- openjdk-8-jdk
- make/gcc (for the npm install that needs to build some stuff).

The mvn installer downloads everthing else. 

To run  Hygieia you need some more packages:

* mongodb
* node/npm
* gulp

The webserver will run both on gulp and apache. You can reach the dashboard at http://localhost:3000 (for gulp) or http://localhost:3001 (for apache)

## Installation
The Dockerfile first installs all apt packages needed and then proceeds with the installation of npm and maven.

A directory is created for the mongodb database and a user hygieiea is added.

The source is cloned and moved to /var/Hygieia after which mvn install is ran.

Finally the configuration of the various components and the supervized config is added (alternatively these can be added with a -v option) and the container is started with the supervizord to start all the different components.

The container needs to be started with a -v localmongodbfiles:/data/db to ensure the mongodb is persistant.

Important: The first time you run the daemon there is no mongodb yet (unless you create one locally first) so it will fail after a short while. You have to create the database then by executing mongo in your container (docker exec -ti hygieia mongo) and running db.use dashboarddb and configure security (see README in the main file for more information)

For our convenience a createdocker bash script was added. That created a new image from the Dockerfile, removes any old Hygieia images and fires up the new one.

To run the docker container by hand run

    docker run -d --publish 3000:3000 --publish 3001:80 --env TZ="Europe/Amsterdam" --name hygieia --hostname hygieia -v /root/docker/mongodb:/data hygieia

## Configuration

In these examples there is no security on the mongo database. Only a dbname is configured! Also, the various other components need to be configured for your purposes of course. 


## Running Hygieia + Sonar + Jenkins
To have a complete set running with Sonar (+mysql) and Jenkins fire up a MySQL, Sonar and Jenkins container first and change the configuration to use the right ip addresses / hostnames in the .properties files.

### Mysql
https://hub.docker.com/_/mysql/

    docker pull mysql
    docker run -d -e MYSQL_ROOT_PASSWORD=password  --name mysql --hostname mysql -v /root/docker/mysql:/var/lib/mysql mysql
    docker exec -ti mysql mysql -ppassword
    create database sonar;
    grant all on sonar.* to 'sonar'@'%' identified by 'sonar';
    grant all on sonar.* to 'sonar'@'localhost' identified by 'sonar';

### SonarQube
https://hub.docker.com/_/sonarqube/

    docker pull sonarqube
    docker run  -d --publish 9000:9000 --name sonarqube --hostname sonarqube -e SONARQUBE_JDBC_USERNAME=sonar -e SONARQUBE_JDBC_PASSWORD=sonar -e SONARQUBE_JDBC_URL=jdbc:mysql://mysql:3306/sonar?useUnicode=true\&amp\;characterEncoding=utf8 --link mysql:mysql -v /root/docker/sonarplugins:/opt/sonarqube/extensions/plugins sonarqube

(make sure the java sonar plugin is in this location, otherwise the java profile is gone after a restart for some weird reason)

### Jenkins
https://hub.docker.com/_/jenkins/

    docker pull jenkins
    docker run -d --publish 8081:8080 --name jenkins --hostname  jenkins -v /root/docker/jenkinshome:/var/jenkins_home -v /root/docker/maven:/var/maven --link mysql:mysql jenkins 

### Hygieia
Start the container you build by running:

    docker run -d --publish 3000:3000 --publish 3001:80 --env TZ="Europe/Amsterdam" --name hygieia --hostname hygieia -v /root/docker/mongodb:/data hygieia

## Compose
Once you've setup the complete set at least once (meaning you got a mongodb and a working mysql/sonar) you can use the docker-compose.yml to start everything at once. Make sure you configure the right local directories in each volume section and then start the containers with:

    docker-compose up

or in the background

    docker-compose up -d
