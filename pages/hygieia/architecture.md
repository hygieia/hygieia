---
title: Architecture Overview
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: architecture.html
---
This diagram offers you the full view of Hygieia from an architectural vantage point.

![Architecture](https://hygieia.github.io/Hygieia/media/images/apiup.png/Hygieia/media/images/architecture.png)

| Layer | Description |
|-------|-------------|
| UI Layer | The UI layer (User Interface) is Hygieia’s front- end and contains all the Graphical User Interface (GUI) elements for users to view. It is here where users are also able to configure the dashboard. |
| API Layer | The Hygieia API layer contains Hygieia APIs and Audit APIs. Hygieia APIs contain all the typical REST API services that work with the source system data (collected by service tasks) and the Internet. Hygieia audit APIs are a collection of API endpoints that serve to audit CI/CD data gathered by Hygieia collectors. This layer is an abstraction of the local data layer and the source system data layer. |
| DevOps Tools | This layer entails the multitude of DevOps tools in a CI/CD pipeline. In the diagram, Jira, Git, Sonar, and XLDeploy are listed as examples. |
| Collectors' Layer | The Collectors’ Layer fetches data from your DevOps tools. In turn, this data then appears on your Hygieia Dashboard. You can choose to install the collectors applicable to your DevOps tool set from the Hygieia Collectors Inventory. |
| Database Layer | Hygieia uses MongoDB as the database for storage and retrieval of data. |


