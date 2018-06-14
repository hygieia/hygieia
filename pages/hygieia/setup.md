---
title: Initial Setup of Hygieia
tags:
keywords:
summary: Instructions to install all components of Hygieia
sidebar: hygieia_sidebar
permalink: setup.html
folder: hygieia

---

## Prerequisites

The following are the prerequisites to set up Hygieia:

- Install Git - Install Git for your platform. For installation steps, see the [**Installing Git** section](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) of Git's documentation.
- Install Java - Version 1.8 is recommended
- Install Maven - Version 3.3.9 and above are recommended

## Download or Clone Hygieia

If you do not already have Hygieia installed, you can download or clone Hygieia from the [GitHub repo](https://github.com/capitalone/Hygieia). For information on cloning a repository, see the [**Cloning** section](https://help.github.com/articles/cloning-a-repository/) of GitHub's Documentation.
 
## Build Hygieia

To package all components of Hygieia's source code into executable JAR files, run the maven build. Before you build Hygieia using Maven, make sure to configure the `settings.xml` file. For more details, see [Proxy Authentication](proxyauthentication.md).

Hygieia uses Spring Boot to package the components as an executable JAR file with dependencies.

To configure Hygieia, execute the following steps:

*	**Step 1: Run Maven Build**

	In the command line/terminal, run the following command from the `\Hygieia` directory of your source code installation:
	 
	```bash
	mvn clean install package
	```

	This will build all the following components:

	~~~
	└── Hygieia
		├── UI
		├── API
		├── AuditAPI
		└── Collectors
			├─ Feature
			│    ├── JIRA
			│    └── VersionOne
			└─ Repos
			'     ├── GitHub
			'     ├── GitLab
			'     ├── Subversion 
			'     └── Bitbucket
			'
			'
			and so on. 		   
	~~~

	The output `.jar` file is generated in the `\target` folder for each component of Hygieia, including collectors.

*	**Step 2: Set Parameters in the Properties File**
	
	Set the configurable parameters in the `.properties` file to connect to each component of Hygieia. For more information about the server configuration, see the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*	**Step 3: Run Each Component**

	To run the executable file for API module, change directory to 'api\target' and then execute the following command from the command prompt:

	```bash
	java -jar api.jar --spring.config.location=C:\[path to]\Hygieia\api\dashboard.properties -Djasypt.encryptor.password=hygieiasecret
	```
	
	To run the UI module, in the command prompt, navigate to `\Hygieia\UI`, and then execute the following command:

	```bash
	gulp serve
	```
	
	The dashboard will serve up on port 3000.
	
	In general, all the collectors can be run using the following command:
	
	```bash
	java -jar <Path to collector-name.jar> --spring.config.name=<prefix for properties> --spring.config.location=<path to properties file location>
	```
	
	The detailed instructions for installing each component of Hygieia is described in the 'Configuration Procedure' section.