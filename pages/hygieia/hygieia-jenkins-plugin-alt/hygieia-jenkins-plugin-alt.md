---
title: Hygieia-Jenkins Plugin Alt
tags: Hygieia
keywords: Hygieia | Jenkins | Plugin Alt
toc: true
summary: Hygieia Jenkins Plugin Alternative - Collect deployment metrics
sidebar: hygieia_sidebar
permalink: hygieia-jenkins-plugin-alt.html
---

#### Synopsis

Hygieia supports deployment collectors for udeploy and xdeploy, it
doesn't provide out of the box collector for Jenkins. Rather Hygieia has
a Jenkins plugin to perform the similar function that the deployment
collector does.

However an enterprise might restrict installation of Hygieia Jenkins
Plugin for a number of reasons - compliance, stability, governance, SOX
etc\...

#### Resolution

To overcome this situation, we came up with a library called
HygieiaJenkinsPluginAlt, which can be invoked by a Jenkins pipeline. It
capture metrics (similar to Hygieia Jenkins plugin) and pushes it to
Hygieia by calling Hygieia API\'s to persist data in Hygieia Mongo
database.

HygieiaJenkinsPluginAlt makes three API calls in order to push
deployment metrics:

```bash
1.  api/build
2.  api/artifact
3.  api/deploy
```
HygieiaJenkinsPluginAlt should be injected within Jenkins pipeline, here
is the code snippet for reference:

![Image](https://hygieia.github.io/Hygieia/media/images/hygieia-jenkins-plugin-alt.png)