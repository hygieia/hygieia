---
title: Collectors
tags:
keywords:
toc: true
summary: Know the basics of installing and configuring Hygieia 
sidebar: hygieia_sidebar
permalink: collectors.html
folder: hygieia
---

## Tool Collectors

Generally, you can run the collectors using the following command:

```bash
java -jar <Path to collector-name.jar> --spring.config.name=<prefix for properties> --spring.config.location=<path to properties file location>
```

You may choose the collectors applicable to your DevOps toolset from the list of supported collectors. In addition, you may write your own collector and plug it in to match your DevOps toolset.

## Supported Collectors

Hygieia supports the following collectors Inventory:

- **Build Collectors**
  - [Bamboo](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/build/bamboo.md)
  - [Jenkins](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/build/jenkins.md)
  - [Jenkins-codequality](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/build/jenkins-codequality.md)
  - [Jenkins Cucumber](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/build/jenkins-cucumber.md)
  - [Sonar](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/build/sonar.md)
- **Cloud Collectors**
  - [AWS](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/cloud/aws.md)
- **Deploy Collectors**
  - [uDeploy](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/deploy/udeploy.md)
  - [XLDeploy](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/deploy/xldeploy.md)
- **Feature Collectors**
  - [Jira](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/feature/jira.md)
  - [VersionOne](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/feature/versionone.md)
  - [Gitlab](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/feature/feature-gitlab.md)
  - [Rally](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/feature/rally.md)
- **Miscellaneous Collectors**
  - [Chat Ops](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/misc/chat-ops.md)
  - [Score](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/misc/score.md)
- **SCM Collectors** 
  - [Bitbucket](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/scm/bitbucket.md)
  - [GitHub](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/scm/github.md)
  - [Gitlab](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/scm/gitlab.md)
  - [Subversion](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/scm/subversion.md)
  - GitHub GraphQL - Currently Unavailable
- **Performance Collector**
  - [AppDynamics](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/performance/appdynamics.md)
- **Configuration Management Database (CMDB)**
  - [HP Service Manager (HPSM)](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/cmdb/hpsm.md)
- **Library Policy**
  - [Nexus IQ](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/library-policy/nexus-iq-collector.md)
  - [WhiteSource](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/library-policy/whitesource.md)
- **Artifact Repository**
  - [Artifactory](https://github.com/Hygieia/Hygieia/blob/gh-pages/pages/hygieia/collectors/artifact/artifactory.md)

## Encrypted Properties

Properties that should not be sorted in plain text are first encrypted and then decrypted to make them intelligible using Jasypt. Encrypted properties are enclosed in the keyword ENC(), i.e., ENC(thisisanencryptedproperty).

To generate an encrypted property, run the following command:

```bash
java -cp ~/.m2/repository/org/jasypt/jasypt/1.9.2/jasypt-1.9.2.jar  org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="dbpassword" password=hygieiasecret algorithm=PBEWithMD5AndDES
```

The values of the command options indicate the following:

- dbpassword - Property value being encrypted

- hygieiasecret - The secret password

When you run the API, this secret password is used as the input value in the following system property for decryption:

```bash
-Djasypt.encryptor.password=hygieiasecret
```

When using Docker, add the environment variable in the following command:

```bash
docker run -t -p 8080:8080 -v ./logs:/hygieia/logs -e "SPRING_DATA_MONGODB_HOST=127.0.0.1" -e "JASYPT_ENCRYPTOR_PASSWORD=hygieiasecret" -i hygieia-api:latest
```

For additional information on encrypting properties in Spring Boot Applications, see jasypt spring boot [documentation](https://github.com/ulisesbocchio/jasypt-spring-boot/blob/master/README.md).

When using GitLab CI Runner, specify the value for JASPYT_ENCRYPTOR_PASSWORD as a secure variable. To add additional secure variables to a GitLab project, go to your project’s **Settings** > **Variable** > **Add Variable**.

**Note**: A secure variable’s value is not visible in the build log and only a project administrator can configure this value.

## Encryption for Private Repos

Encryption for private repositories requires that you generate a secret key and add it to your repository settings files. The steps for encrypting private repositories are as follows:

* **Step 1**: From the core module, generate a secret key.

   ```bash
   java -jar <path-to-jar>/core-2.0.5-SNAPSHOT.jar com.capitalone.dashboard.util.Encryption
   ```

* **Step 2**: Add the generated key to the API properties file.

   ```bash
   #api.properties
   key=<your-generated-key>
   ```

* **Step 3** Add that same generated key to your repository settings file. This key is required for the target collector to decrypt your saved repository password.

   For example, if your repo is GitHub, add the following to the `github.properties` file:

   ```bash
   #github.properties
   github.key=<your-generated-key>
   ```
