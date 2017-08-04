---
title: Architecture Overview
tags:
keywords:
summary:
sidebar: hygieia_sidebar
permalink: architecture.html
---
The following diagram gives an overview of Hygieia Architecture:

## Database Layer

Hygieia uses MongoDB as the database for storage and retrieval of data.

## UI Layer

The UI Layer provides User Interface to view and interact with the configured DevOps Tools. The Hygieia dashboard requires installation of:
- NodeJS
- npm
- gulp
- bower

## API Layer

The API Layer contains all common REST API services that work with source data system data, 
  which has already been collected by other service tasks. This is an abstraction from the local data layer, 
  and the source system data layer.

## Collectors



