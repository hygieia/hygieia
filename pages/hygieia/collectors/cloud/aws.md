---
title: AWS Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: aws.html
---

AWS Collector is part of Hygieia 2.1 release and brings in Ops view to the already dev capabilities of Hygieia, such as DevOps Dashboard (Hygieia 1.0) and Program level Dev View (Hygieia 2.0). The AWS Collector is a microservice with the sole task of collecting data from your AWS footprint for the dashboards configured. As part of our component architecture, this is optional and if you don't use public cloud providers, you don't need to run the AWS Collector.

### Setup Instructions

To configure the AWS Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `aws` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[usernname]\hygieia\collectors\cloud\aws
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```bash
 mvn install
```

The output file `aws-collector.jar` is generated in the `aws\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the AWS Collector.

To configure parameters for the AWS Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `aws-collector.jar` file, change directory to `aws\target`, and then execute the following from the command prompt:

```
java -jar aws-collector.jar --spring.config.name=aws --spring.config.location=[path to application.properties file]
```

### Sample Application Properties File

The sample `application.properties` file lists parameters with sample values to configure the AWS Collector. Set the parameters based on your environment setup.

```properties
		# Database Name
		dbname=dashboarddb

		# Database HostName - default is localhost
		dbhost=localhost

		# Database Port - default is 27017
		dbport=27017

		# MongoDB replicaset
		dbreplicaset=[false if you are not using MongoDB replicaset]
		dbhostport=[host1:port1,host2:port2,host3:port3]

		# Database Username - default is blank
		dbusername=dashboarduser

		# Database Password - default is blank
		dbpassword=dbpassword

		# Logging File location
		logging.file=./logs/cloud.log

		# Collector schedule (required)
		aws.cron=0 0/5 * * * *

		# AWS ValidTag Key - To look for tags that you expect on your resource
		aws.validTagKey[0]=ABC
		aws.validTagKey[1]=XYZ

		# AWS Proxy Host
		aws.proxyHost=localhost

		# AWS Proxy Port
		aws.proxyPort=3333

		# AWS Non Proxy
		aws.nonProxy=xxx.xxx.xxx.xxx

		# AWS Profile to be used, if any
		aws.profile=
```

You don't need to provide credentials in the properties file, the preferred mechanism is using the Default credentials provider mechanism or through an IAM role, as described [here](http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html).

In addition, you can create AWS credentials file using CLI:
```properties
[default]
aws_access_key_id={YOUR_ACCESS_KEY_ID}
aws_secret_access_key={YOUR_SECRET_ACCESS_KEY}
```

If you are running on an EC2 instance in an IAM role with appropriate access defined, then you don't need to provide credentials in the properties file.
