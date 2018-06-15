---
title: Other Users
tags: 
homepage: 
toc: true
sidebar: product1_sidebar
permalink: user_signup.html
---

## User Signup and Login Instructions

To create an account for a new user:

1.	Click **Signup** in the login page to create a user account.
2.	Enter a username, password, confirm the password, and then click **Signup**.

If you already have your login credentials, enter the username and password, and then click **Login**.

**Note:** If SSO authentication is enabled, then on successful authentication, you are automatically logged-in to Hygieia.

## A Note on User Authentication

Hygieia dashboards provide the following options for user authentication:

- LDAP allows you to use your LDAP server to authenticate users. To enable SSO (Single Sign On) authentication, you must configure Hygieia with your LDAP server to authenticate users. 
- Standard authentication uses an internal database of users and passwords. If you choose to create an internal database, the user names and passwords will not be in sync with the LDAP server.

To modify the user authentication type for the dashboard, see the [API Properties](../hygieia/api/api.md#api-properties-file) file.

Before you login to the Hygieia dashboard, choose a login type:
- Standard Login
- LDAP Login

If **Single Sign On (SSO)** authentication is enabled, then the user is automatically authenticated and logged-in to Hygieia.
