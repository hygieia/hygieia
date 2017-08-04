---
title: Set up Jenkins
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: jenkinssetup.html
---
## 3. Set up Jenkins w/cucumber output - by starting a test jenkins master
	a. `docker-compose -f test-servers/jenkins/jenkins.yml up -d`
	b. Run the job: http://192.168.99.100:9100/job/Hygieia_Example_Job/build
	c. Configure the Jenkins Build and Jenkins Cucumber panels using this jobs output.


## Start Collectors in the background (optional as they are all running in containers by default)
* To start individual collector as background processes, use this format:
  * On linux platform
```bash
nohup java -jar <collector-name>.jar --spring.config.name=<property file name> & >/dev/null
```
