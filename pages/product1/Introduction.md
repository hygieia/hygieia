---
title: Hygieia - The DevOps Dashboard
tags: 
type: first_page
homepage: true
toc: false
sidebar: product1_sidebar
permalink: Introduction.html
redirect_from:
  - /index.html
  - /hygieia/Introduction.html
---

## Introduction

Hygieia dashboard is a single, configurable, easy-to-use dashboard to visualize near real-time status of the entire delivery pipeline. In addition, it provides a continuous feedback loop for any DevOps organization.

Hygieia dashboards are customizable; for example, you can select your story tracking tools, code repository, build, quality, and deployment tools for monitoring the delivery pipeline. In addition, plugins are available to enable customization.

Hygieia dashboards assist in achieving process transparency and therefore help establish feedback loops, thus achieving the underlying concept of DevOps and Lean. They contain interactive elements which enable drill-down and linking to the connected tools.

## Audience

The Dashboard Console provides configuration procedures for all the DevOps toolsets that appear on the Console. This document describes the procedure for performing basic configuration.

This guide is intended for a technical audience to implement and support the application. The audience should have knowledge of Java, MongoDB, JavaScript, Git, and RESTful APIs.

## Prerequisites

Before you begin to configure your Dashboard, make sure the following prerequisites are met:

- The following components of Hygieia are configured:
  - Database Layer 
  - UI Layer
  - API Layer
  - Collectors to configure on the Dashboard
  
For detailed instructions on configuring each of the components of Hygieia, see the [Configuration Procedure](http://capitalone.github.io/Hygieia/getting_started.html) section in the Getting Started guide.
  
- If you are configuring Hygieia using Docker, make sure the Docker Instances are up and running for all components of Hygieia.
  For detailed instructions on configuring a Docker Image, see the [Build Docker](../hygieia/Build/builddocker.md) section in the Getting Started guide.
  
Continue setting up your Dashboard if all the prerequisites are met.

