---
title: Collectors
tags:
keywords:
summary: Know the basics of installing and configuring Hygieia 
sidebar: hygieia_sidebar
permalink: collectors.html
folder: hygieia
---

## Tool Collectors
In general, all the collectors can be run using the following command
```bash
java -jar <Path to collector-name.jar> --spring.config.name=<prefix for properties> --spring.config.location=<path to properties file location>
```

You can pick and choose which collectors are applicable for your DevOps toolset or you can write your own collector and plug it in.

Hygieia supports the following collectors Inventory:

- **Build Collectors**
  - [Bamboo](bamboo.html)
  - [Jenkins](jenkins.html)
  - [Jenkins Cucumber](jenkins-cucumber.html)
  - [Sonar](sonar.html)
- **Cloud Collectors**
  - [AWS](aws.html)
- **Deploy Collectors**
  - [uDeploy](udeploy.html)
  - [XLDeploy](xldeploy.html)
- **Feature Collectors**
  - [Jira](jira.html)
  - [VersionOne](versionone.html)
  - [Gitlab](feature-gitlab.html)
- **Miscellaneous Collectors**
  - [Chat Ops](chat-ops.html)
- **SCM Collectors** 
  - [Bitbucket](bitbucket.html)
  - [Github](github.html)
  - [Gitlab](gitlab.html)
  - [Subversion](subversion.html)
- **Performance Collector**
  - [AppDynamics](appdynamics.html)
- **Configuration Management Database (CMDB)**
  - [HP Service Manager (HPSM)](hpsm.html)
- **Library Policy**
  - [Nexus IQ](nexus-iq-collector.html)
- **Artifact Repository**
  - [Artifactory](artifactory.html)