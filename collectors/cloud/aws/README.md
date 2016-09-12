# Hygieia Cloud Collectors / AWS

AWS Collector is part of Hygieia 2.1 release and bring in Ops view to the already dev capabilities of Hygieia like DevOps Dashboard (Hygieia 1.0) and Program level Dev View (Hygieia 2.0). The AWS Collector is a microservice with sole task of collecting data from your AWS footprint for the dashboards configured. Again as part of our component architecture this is optional and if you don't use public cloud providers , you don't need to run this.

##  Supported Cloud platforms.

- Amazon Web Service(AWS).

## Building and Deploying

To package the collector into an executable JAR file, run:
```
mvn install
```

Copy this file to your server and launch it using:
```
java -JAR aws-collector.jar \
  --spring.config.name=aws \
  --spring.config.location=./aws.properties
```

## application.properties

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the AWS collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

### Sample application.properties file

```properties
# Database Name
dbname=dashboard

# Database HostName - default is localhost
dbhost=localhost

# Database Port - default is 27017
dbport=27017

# Database Username - default is blank
dbusername=db

# Database Password - default is blank
dbpassword=dbpass

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

# AWS Profile to be used if any
aws.profile=
```

You don't need to provide credentials in the properties file , the preferred mechanism is via the Default credentials provider mechanism or via an IAM role, as described here:
http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html

You can also create an AWS credentials file using the CLI or hand such as:
```properties
[default]
aws_access_key_id={YOUR_ACCESS_KEY_ID}
aws_secret_access_key={YOUR_SECRET_ACCESS_KEY}
```

If you are running on an EC2 instance with an IAM role with appropriate access defined, that should also be sufficient.
