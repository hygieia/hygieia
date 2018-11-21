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
- Install httpd (Redhat/CentOS/Fedora) , apache2(Debian based distriubution)
- Install Git - Install Git for your platform. For installation steps, see the [**Installing Git**](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) section of Git's documentation.
- Install Java - Version 1.8 is recommended
- Install Maven - Version 3.3.9 and above are recommended

## Download or Clone Hygieia

If you do not already have Hygieia installed, you can download or clone Hygieia from the [GitHub repo](https://github.com/capitalone/Hygieia). For information on cloning a repository, see the [**Cloning a Repository**](https://help.github.com/articles/cloning-a-repository/) section of GitHub's Documentation.
 
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

## Setup httpd (instead of default gulp)
	
Please make sure the httpd is installed, then open the following file and update the below configuration accordingly. 
	
	```# vi /etc/httpd/conf/httpd.conf
	Listen 80
	<VirtualHost *:80>
        ProxyPreserveHost On
        ProxyPass /api http://localhost:8080/api
        #ProxyPassReverse /api http://localhost:8080/api

        ServerName <ServerName>
        ServerAlias is-hygiea-wow *.is-hygieia-wow
        ServerAdmin <email ID>
        DocumentRoot <absolute path of the dist folder>   ## eg. /opt/Dashboard/dist/
	</VirtualHost>
	....
	......
	<Directory "/var/www">
	    AllowOverride None
	    # Allow open access:
	    Require all granted
	</Directory>

	<Directory /opt/Dashboard/dist/>		## replace the path with your dist folder path. 
		Options Indexes FollowSymLinks
		AllowOverride None
		Require all granted
	</Directory>

	```
	
## Start Webserver
	``` # systemctl status httpd
	    # systemctl start httpd
	    # systemctl status httpd
	
	
## Start collectors

*	**Option 1: Run Each Collector in the background**

	To run the executable file for API module, change directory to 'api\target' and then execute the following command from the command prompt:

	```bash
	java -jar api.jar --spring.config.location=C:\[path to]\Hygieia\api\dashboard.properties -Djasypt.encryptor.password=hygieiasecret &
	```
	
	**Option 2: Running all collectors using script
	
	-In general, all the collectors can be run using the following command:
		```java -jar <Path to collector-name.jar> --spring.config.name=<prefix for properties> --spring.config.location=<path to properties file location>```
	- For detailed instructions on installing each component of Hygieia, see the documentation corresponding to each component.
	- Example 
	- Create a script file to start all the collectors 
	- Example
	``` java -jar /opt/Dashboard/github-scm-collector-*.jar --spring.config.name=github -- 	spring.config.location=/opt/Dashboard/application.properties &
```java -jar /opt/Dashboard/jenkins-build-collector-*.jar --spring.config.name=jenkins --spring.config.location=/opt/Dashboard/application.properties &
java -jar /opt/Dashboard/rally-collector-*.jar --spring.config.name=rally --spring.config.location=/opt/Dashboard/application.properties &
java -jar /opt/Dashboard/sonar-codequality-collector-*.jar --spring.config.name=sonar --spring.config.location=/opt/Dashboard/application.properties &

```
	
	
	
