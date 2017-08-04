---
title: Set up SONAR
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: sonarsetup.html
---

## 2. Setup Sonar -  by running a test instance of sonar
	a. `docker-compose -f test-servers/sonar/sonar.yml up -d`
	b. Fill it with data from the Hygieia project
```bash
mvn sonar:sonar -Dsonar.host.url=http://$(docker-machine ip default):9000 -Dsonar.jdbc.url="jdbc:h2:tcp://$(docker-machine ip default)/sonar"
```
	c. You can now go in and configure the quality panel in the UI.
