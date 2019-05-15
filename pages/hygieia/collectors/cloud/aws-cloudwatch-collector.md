---
title: AWS Cloudwatch Collector
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: aws-coudwatch-collector.html
---

AWS Cloudwatch Collector is part of Hygieia 2.1 release and brings in Ops view to the already dev capabilities of Hygieia, such as DevOps Dashboard (Hygieia 1.0) and Program level Dev View (Hygieia 2.0). The AWS Cloudwatch Collector is a microservice with the sole task of collecting data from your AWS cloudwatch logs for the dashboards configured. As part of our component architecture, this is optional and if you don't use public cloud providers, you don't need to run the AWS Cloudwatch Collector.
The intention is to present a simple analysis of any logs that are being sent into cloudwatch, allowing monitoring of the number of events in the specified
logstreams

### Setup Instructions

To configure the AWS Cloudwatch Collector, execute the following steps:

*   **Step 1: Change Directory**

Change the current working directory to the `cloudwatch` directory of your Hygieia source code installation.

For example, in the Windows command prompt, run the following command:

```
cd C:\Users\[usernname]\hygieia\collectors\cloud\cloudwatch
```

*   **Step 2: Run Maven Build**

Run the maven build to package the collector into an executable JAR file:

```bash
 mvn install
```

The output file `aws-cloudwatch-collector-${VERSION}.jar` is generated in the `cloudwatch\target` folder.

*   **Step 3: Set Parameters in Application Properties File**

Set the configurable parameters in the `application.properties` file to connect to the Dashboard MongoDB database instance, including properties required by the AWS Collector.

To configure parameters for the AWS Cloudwatch Collector, refer to the sample [application.properties](#sample-application-properties-file) file.

For information about sourcing the application properties file, refer to the [Spring Boot Documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files).

*   **Step 4: Deploy the Executable File**

To deploy the `aws-cloudwatch-collector-${VERSION}.jar` file, change directory to `cloudwatch\target`, and then execute the following from the command prompt:

```
java -jar aws-cloudwatch-collector-${VERSION}.jar --spring.config.name=cloudwatch --spring.config.location=[path to application.properties file]
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
		cloudwatch.cron=0 0/5 * * * *

        # AWS Profile
        cloudwatch.profile=MY_PROFILE

        # AWS Region
        cloudwatch.region=eu-west-1

		# AWS Proxy Host
		cloudwatch.proxyHost=localhost

		# AWS Proxy Port
		cloudwatch.proxyPort=3333

		# AWS Non Proxy
		cloudwatch.nonProxy=xxx.xxx.xxx.xxx

        # The period to analysis on each run (in minutes)
        cloudwatch.logAnalysisPeriod=1

        # The job structure
        cloudwatch.jobs[0].name=Logins
        cloudwatch.jobs[0].series[0].name=login pass
        cloudwatch.jobs[0].series[0].filterPattern=YOUR_PATTERN 
        cloudwatch.jobs[0].series[0].logGroupName=logGroupName
        cloudwatch.jobs[0].series[0].logStreams[0]=logstream
```

You can also optionally provide a region (note if you don't give it will will default to the java one)
```properties
  # aws region (e.g. eu-west-1)
  cloudwatch.region=eu-west-1
```
A full list of regions can be found [here](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Concepts.RegionsAndAvailabilityZones.html)

You don't need to provide credentials in the properties file, the preferred mechanism is using the Default credentials provider mechanism or through an IAM role, as described [here](http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html).

In addition, you can create AWS credentials file using CLI:
```properties
[default]
aws_access_key_id={YOUR_ACCESS_KEY_ID}
aws_secret_access_key={YOUR_SECRET_ACCESS_KEY}
```

If you are running on an EC2 instance in an IAM role with appropriate access defined, then you don't need to provide credentials in the properties file.

The main section is the jobs structure. Each Job is available for selection by name in the widget configuration. It is therefore important that you give these unique and meaningful name.
Each job can have any number of series. Each series will appear in the UI as a different line on the chart for the job.
Each series can span multiple log stream and will before the filter on the events and count the number of events matching
that filter over the specified logAnalysisPeriod as a point on each series. Hence:
```properties
  cloudwatch.jobs[0].name=Logins
  cloudwatch.jobs[0].series[0].name=login pass
  cloudwatch.jobs[0].series[0].filterPattern=YOUR_PATTERN 
  cloudwatch.jobs[0].series[0].logGroupName=logGroupName
  cloudwatch.jobs[0].series[0].logStreams[0]=logstream
  cloudwatch.jobs[0].series[1].name=login pass
  cloudwatch.jobs[0].series[1].filterPattern=YOUR_OTHER_PATTERN 
  cloudwatch.jobs[0].series[1].logGroupName=logGroupName
  cloudwatch.jobs[0].series[1].logStreams[0]=logstream
```

would present 2 series on a single chart (called `Logins`) with the first one labelled `login pass` that would present the number of 
events that match `YOUR_PATTERN` and another series labelled `login fail` giving the same data for `YOUR_OTHER_PATTERN` 
