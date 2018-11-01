---
title: Build Docker Image 
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: builddocker.html
---

To build a Docker image for all components of Hygieia, execute the following steps:

*	**Step 1: Build the Containers**

	To package the Hygieia source code into an executable JAR file, run the Maven build from the `\Hygieia` directory of your source code installation:

	```bash
	mvn docker:build
	```

*	**Step 2: Start the Container Images**

	Start containers in the background and keep them running:

	```bash
	docker-compose up -d
	```

*	**Step 3: Create a User in MongoDB**

	If you login to the container, then you do not have to install MongoDB locally.

	Execute the following commands to connect to MongoDB, and then add dashboard user:

	```bash
	#Connect to MongoDB
	docker exec -t -i mongodb2 bash
	```
	
	To build the Docker Image for the UI layer, refer to section, [Docker Image for UI Layer](../UI/ui.md#docker-image-for-ui-layer)
	
*	**Step 4: Configure your Environment**

	To configure your environment, create a `docker-compose.override.yml`. The most commonly used properties are listed and the uncommented properties are mandatory for the collector to work:

	```bash
	hygieia-github-scm-collector:
	  environment:
	  - GITHUB_HOST=github.com
	  - GITHUB_CRON=0 * * * * *
	  - GITHUB_COMMIT_THRESHOLD_DAYS=300
	hygieia-jira-feature-collector:
	  environment:
	  - JIRA_BASE_URL=https://mycompany.atlassian.net/
	  - JIRA_CREDENTIALS=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	  - JIRA_ISSUE_TYPE_ID=10200
	  - JIRA_SPRINT_DATA_FIELD_NAME=customfield_10007
	  - JIRA_EPIC_FIELD_NAME=customfield_10008
	hygieia-jenkins-build-collector:
	  environment:
	  - JENKINS_CRON=0 * * * * *
	  - JENKINS_MASTER=http://192.168.99.100:9100
	  - JENKINS_USERNAME=XXXXXXXXXXXXXXXXXXXXXX
	  - JENKINS_API_KEY=XXXXXXXXXXXXXXXXXXXXXXXXXX
	hygieia-jenkins-cucumber-test-collector:
	  environment:
	  - JENKINS_CRON=0 * * * * *
	  - JENKINS_MASTER=http://192.168.99.100:9100
	  - JENKINS_USERNAME=XXXXXXXXXXXXXXXXXXXXXX
	  - JENKINS_API_KEY=XXXXXXXXXXXXXXXXX
	  - JENKINS_CUCUMBER_JSON_FILENAME=cucumber-report.json
	hygieia-sonar-codequality-collector:
	  environment:
	  - SONAR_URL=http://192.168.99.100:9000
	  - SONAR_CRON=0 * * * * *
	```
	**Note**: For dev/testing the project, change the CRON entries to `"0 * * * * *"`.

	For generic Docker configuration properties, refer to the [docker-compose.yml](https://github.com/capitalone/Hygieia/blob/master/docker-compose.yml) file.

*	**Step 5: Restart All Services**

	Ensure there is an existing user at start-up.

	```bash
	#Restarts all stopped and running services
	docker-compose restart
	```

	To get the port for the UI Layer, execute the following command:

	```bash
	docker port hygieia-ui
	```

**Note**: You can build Docker images individually for the API and UI layers. For instructions, refer to the the [API](../api/api.md#docker-image-for-api) and [UI](../UI/ui.md#docker-image-for-ui-layer) documentation.
