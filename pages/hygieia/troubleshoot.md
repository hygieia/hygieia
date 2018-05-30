---
title: FAQs
tags:
keywords:
summary: Frequently Asked Questions
sidebar: hygieia_sidebar
permalink: troubleshoot.html
---

This document contains Frequently Asked Questions (FAQs) about Hygieia.

#### How to configure the GitHub Collector for a Private Repo?

To configure the GitHub collector for a private repository, generate a secret key and add the secret key and your user ID to the API properties file. For detailed instructions, see [Encryption for Private Repos](setup.md#encryption-for-private-repos).

To pass the GitHub authentication information in the `application.properties` file, modify DefaultGitHubClient.java and force the response string to fetch the password from  the properties file:

```bash
//ResponseEntity<String> response = makeRestCall(queryUrlPage, settings.getUserId(), decryptedPassword);
ResponseEntity<String> response = makeRestCall(queryUrlPage, settings.getUserId(), settings.getKey());
```

#### Using the properties ```github.key``` and ```github.personalAccessToken``` for encrypting private GitHub repositories

**github.key**

```github.key``` is the value generated from the core module. You can generate this key from the UI as an **admin** user. The steps are as follows:

1. Create an admin user in Hygieia with useername **admin** and password of your choice. For detailed instructions, see [Create Admin User](../product1/signup.md#create-admin-user).
2. Navigate to Admin Settings -> Generate Api Token, and create a new key by setting a name and expiration date. For detailed instructions, see [Generate API Token](../product1/signup.md#generate-api-token).
3. Copy the generated key to github.properties file as ```github.key="generated key"```.
4. In the api.properties file, add the property, ```key="generated key"```.

The generated key is used to encrypt the password when you configure the GitHub repository for accessing private repos. 
	
**Note**: Public repos do not require values for ```github.key``` and ```github.personalAccessToken```.

**github.personalAccessToken**

The value of the property ```github.personalAccessToken``` is directly taken from GitHub -> Developer Settings-> personalAccessToken. For detailed instructions, see [GitHub](https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/) documentation.

This token is also used for accessing the private repos.

You can either set the value for ```github.key``` or ```github.personalAccesstoken``` in order to access private repos.

In addition, please refer [Encryption for Private Repos](setup.md#encryption-for-private-repos).

#### How to change the UI port to a port other than 3000?

By default, the UI uses port 3000 to display the dashboard. To change the port, make the following changes to the `gulpfile.js`:

```bash
browserSync.init({
          port: 9999,
          server: {
              baseDir: hygieia.dist,
              startPath: '/',
              middleware: [proxyMiddleware]
          },
          ghostMode: ghostMode
      });
```

Restart the API service. In addition, clear the browser cache and cookies or use incognito tab to launch the UI, and then run the following in the command line or terminal:

```bash
gulp serve
```
 
#### How to remove unconfigured widgets from the dashboard?

To remove unconfigured widgets from a dashboard, execute the following:

1. Signup and login as 'admin' user. For detailed instructions, see [Signup and Login](../product1/signup.md).
2. Create a custom template by choosing the widgets that appear on the dashboard. For detailed instructions, see [Manage Dashboard Templates](../product1/signup.md#manage-dashboard-templates).

Only the selected widgets are displayed on the dashboard.

#### Error while building the API layer

When setting up the API layer, the following error is displayed:

```bash
INFO  org.mongodb.driver.cluster - Exception in monitor thread while connecting to server 10.0.0.0:27017
com.mongodb.MongoSocketOpenException: Exception opening socket
```

In this case, make sure to connect to your MongoDB instance and the MongoDB user `dashboarduser` is created with password `dbpassword`.

For detailed instructions on Hygieia database setup based on your platform, see [Database Setup](database.html).

Once the database setup is complete, following steps in the [API Configuration](api/api.md) document to complete the API setup.

#### Product Dashboard - Not seeing data in environment stages (DEV, INT, PROD, PERF, and so on) after configuring the pipeline.

In this case, execute the following steps:

1. Do a dummy commit on your GitHub, run Jenkins build, and then deploy jobs for that repo.
2. Configure build and deploy widgets.
3. Configure pipeline with available environment stages in the dropdown.
4. Add that team dashboard to the product dashboard.
5. Repeat Step 1.
6. Refresh the product dashboard.

**Note:** Make sure the GitHub collector is running to pull the latest commits.

#### In the Feature widget, team names are not populated in the dropdown menu for JIRA.

Set the following properties in your `jira.properties` file:

```bash
feature.jiraBoardAsTeam=true
feature.jiraTeamFieldName = [A non-empty value. For example, customfield_14401]
```

To view the sample application properties for JIRA, see [JIRA Collector](collectors/feature/jira.md#sample-application-properties).

#### Hygieia API build fails due to the following test failure:

```bash
DefaultSecurityTest.registerUser:87 Status expected:<200> but was:<401>
```

In the "test.properties" file, add the property `auth.expirationTime` with a reasonably high value so the JWT token does not expire.

```bash
auth.expirationTime=1200000
```

#### The JIRA widget shows all items as open, even closed/resolved/in progress items.

This is caused when the status information coming back from the JIRA endpoint is not a valid JSON.

Verify the response from the browser using the following URL:

https://<jira base URL from the properties file>/rest/api/2/status/

#### GitHub collector does not pick up proxy information in Docker.

In this case, add the following to the [Dockerfile](https://github.com/capitalone/Hygieia/blob/master/collectors/scm/github/docker/Dockerfile):

```bash
java -Dhttp.proxyHost=docker.for.mac.localhost -Dhttp.proxyPort=8099 -Dhttps.proxyHost=docker.for.mac.localhost -Dhttps.proxyPort=8099 -jar github-scm-collector-2.0.5-SNAPSHOT.jar --spring.config.location=hygieia-github-scm-collector.properties
```