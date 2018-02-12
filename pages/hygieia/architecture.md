---
title: Architecture Overview
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: architecture.html
---
The following diagram gives an overview of Hygieia's Architecture:

![Architecture](http://capitalone.github.io/Hygieia/media/images/architecture.png)

| Layer | Description |
|-------|-------------|
| Database Layer | Hygieia uses MongoDB as the database for storage and retrieval of data. |
| API Layer | Hygieia API layer consists of Hygieia APIs and Audit APIs. Hygieia APIs contain all the common REST API services that work with the source system data (collected by service tasks). Hygieia audit API is a collection of API endpoints that serve to audit CICD data gathered by Hygieia collectors. This layer is an abstraction of the local data layer and the source system data layer. |
| UI Layer | The UI Layer represents Hygieia's front-end and contains GUI elements for users to view and configure the dashboard. |
| Collectors Layer | The Tool Collectors fetch data from your DevOps tools and reflect this data on your Hygieia Dashboard. You can choose to install the collectors applicable to your DevOps tool set from the Hygieia Collectors Inventory. |



