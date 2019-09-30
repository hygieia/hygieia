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

- Install Git - For installation steps, see the [**Installing Git**](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) section of Git's documentation.
- Install Java - Version 1.8 is recommended
- Install Maven - Version 3.3.9 and above are recommended
- Install Node - Version 8 is recommended. 

If you're using a Mac and don't have node already installed, run: 

```bash 
brew install node@8 
```

- Install npm - Version 5 or higher. 

## Download or Clone Hygieia

If you do not already have Hygieia installed, you can fork and clone Hygieia from the [GitHub repo](https://github.com/capitalone/Hygieia). Make sure that you also download the [hygieia-core](https://github.com/Hygieia/hygieia-core) and [api] (https://github.com/Hygieia/api).

For information on forking a repository, see the [**Fork a repo**] (https://help.github.com/en/articles/fork-a-repo) section of GitHub's Documentation.  For information on cloning a repository, see the [**Cloning a Repository**](https://help.github.com/articles/cloning-a-repository/) section of GitHub's Documentation.
 
## Build Hygieia

To package all components of Hygieia's source code into executable JAR files, run the maven build. Before you build Hygieia using Maven, make sure to configure the `settings.xml` file. For more details, see [Proxy Authentication](proxyauthentication.md).

Hygieia uses Spring Boot to package the components as an executable JAR file with dependencies.

** Note: 
The collectors are being migrated to their own repositories. Please see Module breakout section of [Hygieia-2019-Roadmap-(draft,-work-in-progress)](https://github.com/Hygieia/Hygieia/wiki/Hygieia-2019-Roadmap-(draft,-work-in-progress)) for the latest information on the repositories. Documentation will be updated after the migration is complete.

To configure Hygieia, execute the following steps:

*	**Step 1: Run Maven Build**

	First you must build the Hygieia core. In the command line/terminal, run the following command from the `\hygieia-core` directory:
	
	```bash 
	mvn clean install 
	```
	Once you have built the Hygieia core, navigate to `\Hygieia\UI` and run: 

	```bash 
	npm install
	```  
	Installing npm ensures that you have the necessary dependencies.  
	
	Next you must build the api. In the command line/terminal, run the following command from the `\api` directory:
	
	```bash 
	mvn clean install 
	```
	
	After the core and api are successfully built, you will be able to build the Hygieia project. In the command line/terminal, run the following command from the `\Hygieia` directory of your source code installation:
	 
	```bash
	mvn clean install 
	```

	This will build all the following components:

	~~~
	└── Hygieia (https://github.com/Hygieia/Hygieia)
		├── UI
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
	java -jar api.jar --spring.config.location=C:\[path to api.properties file] -Djasypt.encryptor.password=hygieiasecret
	```

	To run the UI module, in the command prompt, navigate to `\Hygieia\UI`.  If you do not have gulp already installed, run the following command: 

	```bash
	npm install gulp@3.9.1
	```
	
	Then execute the following command:

	```bash
	gulp serve
	```
	
	The dashboard will serve up on port 3000.

	Tip: If there are difficulities loading your UI, try cleaning up your cookies by opening up your local dashboard in incognito mode.  
	
	In general, all the collectors can be run using the following command:
	
	```bash
	java -jar <Path to collector-name.jar> --spring.config.name=<prefix for properties> --spring.config.location=<path to properties file location>
	```
	
	For detailed instructions on installing each component of Hygieia, see the documentation corresponding to each component.
