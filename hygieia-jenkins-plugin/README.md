# Hygieia℠ plugin for Jenkins - (started with Slack publisher)

# Developer instructions

Install Maven and JDK.  This was last built with Maven 3.2.5 and OpenJDK
1.7.0\_75 

Run unit tests

    mvn test

Create an HPI file to install in Jenkins (HPI file will be in `target/hygieia-publisher.hpi`).

    mvn clean package 

If build fails due to a maven error that looks like this:

`[ERROR] Failed to execute goal on project hygieia-publisher: Could not resolve dependencies for project org.jenkins-ci.plugins:hygieia-publisher:hpi:1.3-SNAPSHOT: Could not find artifact com.capitalone.dashboard:core:jar:2.0.2-SNAPSHOT in anonymous (https://mycompany.nexus.com/nexus/content/groups/CLM) -> [Help 1][ERROR]`

Clone Hygieia root, `cd` to `core`, and do `mvn clean install` before building this plugin.


# Important
This plugin uses the Hygieia core package. The main project is JDK 1.8 compiled, if you have Jenkins running on previous Java versions, make sure to recompile core package with that previous version and then build this Jenkins plugin.

# Jenkins instruction

1. Install the plugin by using "Advanced" option in Jenkins Plugin Management option to manually upload the file from local disk.
2. Restart jenkins.
3. Configure Global Hygieia Publisher in Jenkins Manage Jenkins/Configure System. Enter Hygieia API url such as `http://localhost:8090/api`. There is no API token implented at this time and it is work in progress.

![Image](../media/images/jenkins-global.png)

4. For a build job, add a Post build action "Hygieia Publisher". 
5. Select what to send to Hygieia. Currently, "Build", "Artifact Info", "Sonar Anslysis", "Deployment" and "Cucumber Test Results" can be published. 

![Image](../media/images/jenkins-job-config.png)

