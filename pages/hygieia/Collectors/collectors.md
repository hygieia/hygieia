---
title: Collectors
tags:
keywords:
summary: Know the basics of installing and configuring Hygieia 
sidebar: hygieia_sidebar
permalink: collectors.html
folder: kb
---

## Tool Collectors
In general, all the collectors can be run using the following command
```bash
java -jar <Path to collector-name.jar> --spring.config.name=<prefix for properties> --spring.config.location=<path to properties file location>
```

You can view the collector inventory [here](collectors/README.md).

You can pick and choose which collectors are applicable for your DevOps toolset or you can write your own collector and plug it in.

Hygieia supports the following collectors Inventory:

- **Build Collectors**
  - Bamboo
  - Jenkins
  - Jenkins Cucumber
  - Sonar
- **Cloud Collectors**
  - AWS
- **Deploy Collectors**
  - uDeploy
  - XLDeploy
- **Feature Collectors**
  - Jira
  - VersionOne
- **Miscellaneous Collectors**
  - Chat Ops
- **SCM Collectors** 
  - Bitbucket
  - Github
  - Gitlab
  - Subversion
