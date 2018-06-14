---
title: Hygieia-Jenkins Plugin
tags:
keywords: 
toc: true
summary: Jenkins Plugin for Hygieia
sidebar: hygieia_sidebar
permalink: hygieia-jenkins-plugin.html
---

Hygieia collectors are classified into the following types for data collection:

- Pull-based Collectors - These collectors pull all information from the DevOps tools.
- Push-based Collectors - These collectors collect a subset of data from the DevOps tools.

The Hygieia-Jenkins plugin is a push-based collector that supports the Jenkins pipeline code for continuous integration and delivery. You can use the Hygieia-Jenkins plugin to publish data from Jenkins to the Hygieia dashboard. You can publish build and artifact information, Sonar test results, deployment results, and Cucumber test results. Therefore, you need not run the corresponding collectors if you use Jenkins for build, deploy, Sonar analysis, and Cucumber tests.

The Hygieia-Jenkins plugin requires installation of:

- Maven (version 3.3.9 and above are recommended)
- JDK (version 1.8 is recommended)

To configure the Hygieia-Jenkins Plugin, execute the following steps:

*	**Step 1: Run Unit Test Cases**

	From your project's root directory, run the unit test cases to check Hygieia code:

	```bash
	mvn test
	```
	
*	**Step 2: Create HPI File**

	Create an HPI file to install in Jenkins. The HPI file is stored at `\Hygieia\hygieia-jenkins-plugin\target\hygieia-publisher.hpi`. To build the Hygieia-Jenkins Plugin, execute the following command:

	```bash
	mvn clean package
	```
	
	The output file `hygieia-publisher.jar` is generated in the `\hygieia-jenkins-plugin\target` folder.

**Note**: The main project is compiled using JDK v1.8. If you are running Jenkins on Java versions prior to Java v1.8, recompile Hygieia’s core package with the prior version, and then build the Jenkins plugin.

### Jenkins 2.0 with Pipeline

To install the plugin in Jenkins:

1. In the Jenkins toolbar, go to ** tab.
2. In the **Upload Plugin** section, click **Choose File**, navigate to the `\hygieia-jenkins-plugin\target` folder, and then select the `hygieia-publisher.hpi` file. Click **Upload**. 
   
   Once the plugin is installed, you can view the plugin listed on the **Installed** tab.
3. Restart Jenkins.

4. Next, configure **Global Hygieia Publisher** in Jenkins.

   In the Jenkins toolbar, go to **Manage Jenkins** > **Configure System**. In the Jenkins URL, Enter the Hygieia API URL, `http://localhost:8080/api`.

5. Navigate to **New Page** > **Jenkins Pipeline Syntax** page. Here, you can view all of Hygieia's publishing steps:

![Image](http://capitalone.github.io/Hygieia/media/images/jenkins2.0-steplist.png)

6. Select a step, such as, **Hygieia Deploy Step**, from the list of available steps, fill in the other required information, and then  click **Generate Pipeline Script**. Finally, copy the generated script to the pipeline script.

![Image](http://capitalone.github.io/Hygieia/media/images/jenkins2.0-hygieia-deploy-step.png)

7. The following screenshot shows a simple pipeline script with Maven build, Hygieia artifact, and deployed publishing:

![Image](http://capitalone.github.io/Hygieia/media/images/jenkins2.0-pipeline-deploy-publish.png)

### Jenkins (Versions Prior to 2.0) with Pipeline

To install the plugin for Jenkins:

1. In the Jenkins toolbar, navigate to **Manage Plugins** > **Advanced Tab**.
2. In the **Upload Plugin** section, click **Choose File**, go to the `\hygieia-jenkins-plugin\target` folder, and then select the `hygieia-publisher.hpi` file. Click **Upload**. 
   
   Once the plugin is installed, you can view the plugin listed on the **Installed** tab.
3. Reboot Jenkins.
4. Configure **Global Hygieia Publisher** in Jenkins.

   In the Jenkins toolbar, go to **Manage Jenkins** > **Configure System**. In the Jenkins URL, enter the Hygieia API URL, `http://localhost:8080/api`.

![Image](http://capitalone.github.io/Hygieia/media/images/jenkins-global.png)

5. For a build job, add the Post build action **Hygieia Publisher**. 
6. Select the data to be sent to Hygieia. Currently, **Build**, **Artifact Info**, **Sonar Analysis**, **Deployment**, and **Cucumber Test Results** can be published.

![Image](http://capitalone.github.io/Hygieia/media/images/jenkins-job-config.png)

### Troubleshooting Instructions

The Hygieia-Jenkins plugin build fails due to the following maven error:

`[ERROR] Failed to execute goal on project hygieia-publisher: Could not resolve dependencies for project org.jenkins-ci.plugins:hygieia-publisher:hpi:1.3-SNAPSHOT: Could not find artifact com.capitalone.dashboard:core:jar:2.0.2-SNAPSHOT in anonymous (https://mycompany.nexus.com/nexus/content/groups/CLM) > [Help 1][ERROR]`

In this case, before you build the Hygieia-Jenkins Plugin, clone Hygieia root, change directory to `\Hygieia\core`, and then execute the following command:

```bash
mvn clean install
```
