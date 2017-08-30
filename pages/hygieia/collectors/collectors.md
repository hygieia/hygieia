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
  - [Bamboo](build/bamboo.md)
  - [Jenkins](build/jenkins.md)
  - [Jenkins Cucumber](build/jenkins-cucumber.md)
  - [Sonar](build/sonar.md)
- **Cloud Collectors**
  - [AWS](cloud/aws.md)
- **Deploy Collectors**
  - [uDeploy](deploy/udeploy.md)
  - [XLDeploy](deploy/xldeploy.md)
- **Feature Collectors**
  - [Jira](feature/jira.md)
  - [VersionOne](feature/versionone.md)
  - [Gitlab](feature/feature-gitlab.md)
- **Miscellaneous Collectors**
  - [Chat Ops](misc/chat-ops.md)
- **SCM Collectors** 
  - [Bitbucket](scm/bitbucket.md)
  - [Github](scm/github.md)
  - [Gitlab](scm/gitlab.md)
  - [Subversion](scm/subversion.md)
- **Performance Collector**
  - [AppDynamics](performance/appdynamics.md)
- **Configuration Management Database (CMDB)**
  - [HP Service Manager (HPSM)](cmdb/hpsm.md)
- **Library Policy**
  - [Nexus IQ](library-policy/nexus-iq-collector.md)
- **Artifact Repository**
  - [Artifactory](artifact/artifactory.md)
