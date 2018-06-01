---
title: Database Schema
tags: 
homepage: 
toc: true
sidebar: hygieia_sidebar
permalink: dbschema.html
---

Hygieia uses MongoDB as the database to store and retrieve data. The data is stored in documents and similarly structured documents are organized into collections. The following table gives a list of collections in Hygieia:

| Collections | Description |
|-------------|-------------|
| artifacts | Binary artifacts produced by build jobs and stored in an artifact repository |
| authentication | Serves as the model for storing credentials used for login and signup |
| builds | Contains the result of a Continuous Integration build execution that typically produces binary artifacts. Often triggered by one or more SCM commits. |
| cloud_instance | Represents an EC2 instance from AWS |
| cloud_instance_history | Represents the trending history of EC2 instance from AWS |
| code_quality | Represents code quality at a specific point in time. This could include a unit test run, a security scan, static analysis, functional tests, manual acceptance tests or bug reports. |
| collector_items | Represents a unique collection in an external tool. For example, for a CI tool, the collector item would be a Job. For a project management tool, the collector item might be a Scope. Each collector is responsible for specifying how it's ```CollectorItem(s)``` are uniquely identified by storing key/value pairs in the options Map. The description field will be visible to users in the UI to aid in selecting the correct ```CollectorItem``` for their dashboard. Ideally, the description will be unique for a given collector. |
| collectors | The collectors that have been registered in the given Dashboard application instance |
| commits | A specific commit in a version control repository |
| components | A self-contained, independently deployable piece of the larger application. Each component of an application has a different source repo, build job, deploy job, etc. |
| dashboards | A collection of widgets, collectors and application components that represent a software project under development and/or in production use. |
| environment_components | Represents deployable units (components) deployed to an environment. |
| environment_status |Represents the status (online/offline) of a server for a given component and environment |
| feature | Represents a feature (story or requirement) of a component |
| feature-history | Represents a trending history of features (story or requirement) from a content management system |
| gitrequests | Represents the information from git repository (commits, issues, PRs, etc.) |
| pipelines | Document containing the details of a Pipeline for a ```TeamDashboardCollectorItem``` |
| scope | Represents a project in a content management system that aligns features under scope |
| scope-owner | Represents a team in a content management system that works with features |
| services | A product or service offered by an Application |
| team | Represents a Tempo Team in Jira |
| templates | A collection of templates represent a software project under development and/or in production use |
| test_results | Represents a collection of test suites that have been executed. This may include unit test run, security scan, static analysis, functional tests, manual acceptance tests, or bug reports. |