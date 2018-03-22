---
title: Widget View
tags: 
type: 
homepage: 
toc: true
sidebar: product1_sidebar
permalink: widget_view.html
---

The widget view enables you to configure widgets with various DevOps tools that are used to manage your CI/CD pipeline. The widgets are highly flexible, meaning you can choose to configure the necessary widgets to view your DevOps pipeline on the Hygieia dashboard. These widgets are integrated with Collectors to showcase information, collected from the CI/CD tools, on the Hygieia dashboard.

## Standard Dashboard Widgets

This section gives a high-level overview of the standard dashboard widgets available in a team dashboard. 

|Widget | Description | Supported Collectors |
|-------|-------------|----------------------|
|Features | The Features widget displays Epics or Issues in the current sprint to help you track epics or issues based upon a sprint type using the feature management tools. | Jira, VersionOne, GitLab Feature |
|Code Repo | The Code Repo widget displays graphical representations and summary of the code contribution activities from one of the supported code repositories. The code contribution activities include commits to the repository, number of issues raised, number of pull requests submitted per day, and the current coding trend. | GitHub, Subversion, Bitbucket, GitLab |
|Build | The Build widget shows the build status – which is either a success or a failure – and the average duration of time of the builds over the course of the last 14 days. | Jenkins, Bamboo |
|Quality | The Quality widget displays the code quality details based on unit and functional test results in a tabular format. Each tab displays static code analysis, detection of security issues, license and security issues, and functional test results in Open Source projects. | Sonar, Fortify, Nexus IQ |
|Performance | The Performance widget displays the performance matrix of the application. That means, this widget tracks the overall health of business transactions, node health, as well as the health of HTTP traffic in the application. | AppDynamics
|Deploy | The Deploy widget displays deployment and environment status details, including information about artifacts and server(s). | UDeploy, XLDeploy |
|Monitor | The Monitor widget displays the monitor details that track the status of services such as APIs, GitHub URLs, or Jenkins. This widget displays the status based on the service HTTP code. | Any URL |
|ChatOps | The ChatOps widget displays the collaborative details from the chat engine. | HipChat |

## Configure Widgets - Common Procedure

To configure a widget in the dashboard:

1. Click **Configure Widget** under the widget name for which you want to configure your DevOps Dashboard.

   The 'Configure [Widget Name] Widget' dialog box is invoked.
   
2. In the 'Configure [Widget Name] Widget' screen, enter configuration values for the fields, and then click **Save**.

   The Dashboard widget displays values based on your configuration.

To change widget settings:

- Click the Settings icon, make any configuration changes, and then click **Save**.

  The widget displays details based on the new configuration.
  
To clear a configured widget:

- Click the Delete icon beside the template name. System prompts a message to confirm or cancel deletion. Click Delete to confirm deletion.
  The widget configuration is cleared.

### Configure Feature Widget

1. Click **Configure widget** to invoke the 'Configure Feature Widget' screen. In this screen, enter the following details:
   - Agile Content Tool Type - Select one of the following feature data source:
     - JIRA
     - VersionOne
	 - Gitlab Feature
   - Project Name - Enter a new project name or select one of the existing projects from the list of projects.
   - Team Name - Enter a team name
   - Estimate Metrics are:
     - Hours
     - Issue Count
     - Story Points
   - Sprint Type - Select a sprint type:
     - Sprint
     - Kanban
     - Both
   - List Feature Type - Select a feature type:
     - Epics
     - Issues

2. Click **Save** to save and view the Feature-related details in the widget.

Based on the configuration, Hygieia displays the following details in the Features widget:

- Total number of features in a sprint type
- Total number of features that are in Progress
- Total number of features that have been completed for a sprint
- List of all features that are in progress

### Configure Repo Widget

1. Click **Configure widget** to invoke the 'Configure Repo Widget' screen. In this screen, enter the following details:
   - Select the Repo Type from the dropdown list:
     - GitHub
	 - Subversion
	 - Bitbucket
	 - Gitlab
   - Enter the Repo URL for the selected Repo Type
   - Enter the Branch for the repo. The repo branch is not applicable for Subversion.
   - Enter the repo login credentials. The login credentials is not applicable for Gitlab.
   - Enter the API Key for a private Gitlab repo.
   
2. Click **Save**. The Repo Widget is configured for the selected repo.

Hygieia displays the following details in the Repo widget:

- Graphical representation of the number of issues, pulls and commits per day, versus time interval.
- Summary of commits, pulls, issues, committers, contributors, and ideators for the current day, the previous week, and the last two weeks.

### Configure Build Widget

The Jenkins collector displays build-related details in the build widget. To configure the build widget:

1. Click **Configure widget** to invoke the 'Configure Build Widget' screen. In this screen, enter the following details:
   - Enter the Build Job 
   - Enter the Build Duration Threshold (in minutes). The default duration is 3 minutes.
   - Alert Takeover Criteria indicates the number of consecutive build fails. The default number is 5.
2. Click **Save**. The Build Widget is configured for the selected repo.

Hygieia displays the following details in the Build widget:

- Graphical representation of the number of issues, pulls and commits per day, versus time interval.
- Summary of commits, pulls, issues, committers, contributors, and ideators for the current day, the previous week, and the last two weeks.
- Total number of builds in the current day, the previous week, and the last two weeks.

### Configure Quality and Performance Widget

Code quality related details for functional and unit tests are displayed in this widget based on your configuration of Sonar, Fortify, Nexus IQ, or Errata Code collectors. To configure the quality widget on the dashboard:

1. Click **Configure widget** to invoke the 'Configure Code Analysis Widget' screen. In this screen, enter the following details:

   - Static Code Analysis
   - Security Scan
   - Open Source Scan
   - Functional Tests
2. To configure functional tests:
   - Click the add button, enter a name for the test, and then select a functional test from the drop-down list.
3. Click **Save**. The code quality widget is configured for the selected code analysis job.

Hygieia displays the following details in the Quality widget:

- Static code analysis to display total number of issues and issue types
- Status of unit tests
- Code coverage
- Security Analysis
- Functional tests

Click **VIEW ALL** to view the code quality analysis details in your configured code quality tool.

The AppDynamics collector diplays performance-related details in this widget. To configure the Performance widget:

- In the Performance tab, click **Configure widget** to invoke the 'Configure Performance Widget' screen. In this screen, enter the following details:
   - Select a Performance Analysis Job from the drop-down and then click **Save**.
   
In the Performance widget, Hygieia displays performance details based on unit and functional test results. 
   
### Configure Deploy Widget

Code deployment details are displayed in this widget based on your configuration of UDeploy, XLDeploy, or Jenkins collectors. To configure the deploy widget on the dashboard:

1. In the Performance tab, click **Configure widget** to invoke the 'Configure Deploy Widget' screen. In this screen, enter the following details:

   - Select the deployment application.
   - Enter the criteria to ignore the environment failures pattern.
   - Check the 'Aggregate servers' box to avoid server duplication.
2. Click **Save**.

Hygieia displays the following details in the Deploy widget:

- Application deployment date
- List of all environments deployed for the application
- Last updated date for each environment
- Number of servers up or down in each environment

Click an application environment to view additional artifact details from your application deployment tool, including:
 
- Last updated date of the selected application environment
- Deployment date for the artifact
- Version Number of the artifact
- Server name
- server status (up or down)

Click 'View in [deployment tool name]' to view the application environment details in the deployment tool.

### Configure Monitor Widget

1. Click **Configure widget** to invoke the 'Monitor Configuration' screen. In this screen, enter the following details:
   - In 'Our Services' section, enter the service name to appear on the widget.
   - Enter the service URL you want to monitor on the dashboard.
     Click the add button to add additional services.
   - In the 'Dependent Services' section, select any dependent service that you want to monitor from the drop-down list.
     Click the add button to add any additional services.
2. Click **Save**.

The monitor widget displays a list of services with the service status. Click **ICON LEGEND** to view the icon description and the corresponding HTTP code.
Click on each service listed in the widget to add or modify the service status.

### Configure ChatOps Widget

1. Click **Configure widget** to invoke the 'Configure ChatOps Widget' screen. In this screen, enter the following details:
   - Select HipChat as the chat engine from the drop-down list.
   - Enter the URL to the ChatOps Server.
   - Enter Room Name and Room Auth Token to connect to the chat engine.
2. Click **Save**.
