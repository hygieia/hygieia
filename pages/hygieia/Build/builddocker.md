---
title: Build Docker
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: builddocker.html
---

### Build Docker images and setup id for mongodb

* Build the containers

```bash
mvn docker:build
```

* Bring up the container images

```bash
docker-compose up -d
```

* Create a user in Mongo (if you log into the container then you don't have to install Mongo locally)

```bash
docker exec -t -i mongodb2 bash
```
```bash
mongo 192.168.64.2/admin  --eval 'db.getSiblingDB("dashboard").createUser({user: "db", pwd: "dbpass", roles: [{role: "readWrite", db: "dashboard"}]})'
```

## Create a `docker-compose.override.yml` to configure your environment
These are the most common entries, the uncommented ones are mandatory if you want the collector to work.
For dev/testing you will find it useful to change the CRON entries to ``"0 * * * * *"``
```
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

* Make sure everything is restarted - _it may fail if the user doesn't exist at start-up_

```bash
docker-compose restart
```

* Get the port for the UI

```bash
docker port hygieia-ui
```
