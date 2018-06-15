---
title: How to Test the Installation?
tags:
keywords:
toc: true
summary:
sidebar: hygieia_sidebar
permalink: gitsetup.html
---

This section provides information on how to test the installation for:

- Git
- Sonar
- Jenkins with Cucumber.

#### Set up Git

For the git set up to work, first clone the repo and set your credentials on the ID with which you are running the collectors.

To set up Git, configure Git to point to Hygieia's master branch on GitHub:

	1. In the SCM panel, select `_git_`.
	2. Enter the URL, `https://github.com/capitalone/Hygieia.git`.
	3. Set the branch to `master`.
	
#### Setup Sonar 

To set up Sonar, execute the following steps:

	1. Run a test instance of Sonar: 
	  
	  ```bash
	   docker-compose -f test-servers/sonar/sonar.yml up -d.
	   ```
	2. Populate the test instance with data from the Hygieia project:	
       
	   ```bash
       mvn sonar:sonar -Dsonar.host.url=http://$(docker-machine ip default):9000 -Dsonar.jdbc.url="jdbc:h2:tcp://$(docker-machine ip default)/sonar"
       ```

	3. Finally, configure the quality panel in the UI.

#### Set up Jenkins with a Cucumber Output

To set up Jenkins with a Cucumber output:

	1. Start a test Jenkins master:
	
	```bash
	`docker-compose -f test-servers/jenkins/jenkins.yml up -d`
	
	2. Run the job: 
	
	```bash
	http://192.168.99.100:9100/job/Hygieia_Example_Job/build
	```
	
	3. Configure the Jenkins Build and Jenkins Cucumber panels using the job's output.


#### Start Collectors in the Background

Starting collectors is optional because by default, they are all running in containers.

To start individual collector as background processes, use the following format on the Linux platform:
  
```bash
nohup java -jar <collector-name>.jar --spring.config.name=<property file name> & >/dev/null
```
	