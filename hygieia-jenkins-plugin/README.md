# Hygieia plugin for Jenkins - (started with Slack publisher)

# Developer instructions

Install Maven and JDK.  This was last build with Maven 3.2.5 and OpenJDK
1.7.0\_75 

Run unit tests

    mvn test

Create an HPI file to install in Jenkins (HPI file will be in `target/hygieia-publisher.hpi`).

    mvn clean package 

# Jenkins instruction

1. Install the plugin by using "Advanced" option in Jenkins Plugin Management option to manually upload the file from local disk.
2. Restart jenkins.
3. Configure Global Hygieia Publisher in Jenkins Manage Jenkins/Configure System. Enter Hygieia API url such as `http://localhost:8090/api`. There is no API token implented at this time and it is work in progress.
4. For a build job, add a Post build action "Hygieia Publisher". Select to send build data to Hygieia. Can also send artifact info. Test data pubishing is work in progress.
5.
