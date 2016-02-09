[![Build Status](https://travis-ci.org/capitalone/Hygieia.svg?branch=master)](https://travis-ci.org/capitalone/Hygieia)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/de1a2a557f8e458e9a959be8c2e7fcba)](https://www.codacy.com/app/amit-mawkin/Hygieia)
[![Maven Central](https://img.shields.io/maven-central/v/com.capitalone.dashboard/Hygieia.svg)](http://search.maven.org/#search%7Cga%7C1%7Ccapitalone)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Join the chat at https://gitter.im/capitalone/Hygieia](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/capitalone/Hygieia?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

<img src="https://pbs.twimg.com/profile_images/461570480298663937/N78Jgl-f_400x400.jpeg" width="150";height="50"/>![Image](/UI/src/assets/images/Hygieia_b.png)
--------------------

### Setting up Sonar
For teting you can run everything on the same host.

Run the yml file to create a sonar instance for testing:
```
docker-compose -f sonar.yml up -d
```

This will create a sonar instance on port 9000 of your docker host.

You will want to update the SONAR_URL in your docker-compose.override.yml with the IP of this host if it is not the same one you are installing Hygieia

To load data into sonar for testing you can build as follows assumihg you have installed sonar on 'default' docker machine
```
mvn  sonar:sonar -Dsonar.host.url=http://$(docker-machine ip default):9000 -Dsonar.jdbc.url="jdbc:h2:tcp://$(docker-machine ip default)/sonar"

```

On the Hygieia project for test data.

