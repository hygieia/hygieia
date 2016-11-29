# Hygieia Feature Collectors / Gitlab (Community Contribution)

Collect features/issues from Gitlab issue boards to display on the dashboard. Issue boards were introuduced to Gitlab in version 8.11, so you must be on this version of Gitlab or later to take advantage of this functionality.  

This collector will retrieve all the issues for you project, and classify them based on your issue board(s).  (Gitlab Enterprise Edition allows you to have multiple boards for a project.) By default, Gitlab provides you with two columns on your board, "Backlog", and "Done", you can then customize the columns in between.  The collector works by finding the "lists" you have created for the board, and finding all the issues you have that belong to those lists, and classifying them as "In Progress".  Any issues which are "Closed" are classified as "Done".

Hygieia's UI has two different ways of displaying issue boards, Kanban or Scrum.  The collector determines whether an issue is Kanban or Scrum based on Gitlab's Milestones.  If an issue is associated with a Milestone, and the Milestone also has an end date, the issue will be shown as Scrum, otherwise it will be displayed as Kanban.  The reason for this is that Scrum has set deadlines, which we are using Milestones with deadlines to represent.  Kanban on the other hand is just a backlog organized by priority with no end date.    

This project uses Spring Boot to package the collector as an executable JAR with dependencies.

## Building and Deploying

To package the collector into an executable JAR file, run:
```bash
mvn install
```

Copy this file to your server and launch it using:
```
java -JAR gitlab-feature-collector.jar
```

## application.properties

You will need to provide an **application.properties** file that contains information about how to connect to the Dashboard MongoDB database instance, as well as properties the Gitlab collector requires. See the Spring Boot [documentation](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config-application-property-files) for information about sourcing this properties file.

### Sample application.properties file

``` 
# Database Name
dbname=dashboard

# Database HostName - default is localhost
dbhost=localhost

# Database Port - default is 27017
dbport=27017

# MongoDB replicaset
dbreplicaset=[false if you are not using MongoDB replicaset]
dbhostport=[host1:port1,host2:port2,host3:port3]

# Database Username - default is blank
dbusername=db

# Database Password - default is blank
dbpassword=dbpass

# Logging File location
logging.file=./logs/gitlab.log

#Collector schedule (required)
gitlab.cron=0 0/1 * * * *

#Gitlab host (optional, defaults to "gitlab.com")
gitlab.host=gitlab.company.com

#Gitlab protocol (optional, defaults to "http")
gitlab.protocol=http

#Gitlab port (optional, defaults to protocol default port)
gitlab.port=80
  
#Gitlab API Token (required, must be an admin account to retrieve all teams for the instance of gitlab.  If not admin, will only retrieve teams the user belongs to)
gitlab.apiToken=

```