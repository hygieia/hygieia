---
title: Admin User
tags: 
homepage: 
toc: true
sidebar: product1_sidebar
permalink: signup.html
---

## About Admin Users

Hygieia dashboards provide administrator and user access through various views. An admin user can:

- Select a theme for the dashboard
- Manage user and admin accounts for the dashboard
- Set up API tokens for authentication
- Create and manage custom dashboard templates

### Create Admin User

Once you have installed the UI layer in Hygieia, create a user with username **admin**. This allows the admin user to manage and maintain a level of control on the dashboard users. For each instance, there can only be one **admin** user.

**Note**: You must create an **admin** user in Hygieia to manage and maintain other dashboard users.

To create an account for an **admin** user:
1. Click **Signup** on the login page.
2. Enter **admin** as the username, specify and confirm the password, and then click **Signup**.

The **admin** user is created for the dashboard.

### Admin Settings

Click the Settings wheel at the top-right corner of the screen to manage the users and dashboard settings. The settings are categorized in to the following tabs:

- Edit Dashboards
- Manage Admins
- Generate Api Tokens
- Manage Templates
- General Configurations 

#### Edit Dashboards

This tab lists all the dashboards in Hygieia. As an admin user, you can edit or delete dashboards from this tab as follows:

- To edit a dashboard, click the **Edit** icon to the right of the dashboard name. This invokes the **Administer your Dashboard** screen. For information on this screen, see the [Dashboard Administration](dashboard_administration.md) documentation.
- To delete a dashboard, click the **Delete** icon to the right of the dashboard name, and then confirm deletion by clicking **Delete**.  

#### Manage Administrators

In the **Admin** screen, the **Manage Admins** tab displays a list of all users. To add additional dashboard administrators:

- In the **Users** column, select a user, and then click the right-arrow button.
  The username is displayed in the **Admin** column.

To find users, filter the list by entering all or part of a user name in the 'Search' field.

To remove an admin:

- In the **Admin** column, select an admin, and then click the right-arrow button.
  The username is displayed in the **Admin** column.

#### Generate API Token

Generate an API token for basic authentication to secure APIs. To generate an API token:

1. Click **New**. The **Generate API Token** dialog box in invoked.
2. Enter the API User name.
3. Select an Expiration Date using the calendar button.
4. Click **Create**. The generated API key is displayed.

**Note:** The API key is visible only until the 'Generate API Token' dialog box is open.

Copy the API token to the [API properties](../hygieia/api/api.md#api-properties-file) file.

To know more about securing basic authentication for APIs, see [Secure APIs Basic Authentication](../hygieia/api/api.md#secure-apis-basic-authentication).

#### Select a Dashboard Theme

Select one of the following themes for the dashboard:
- Dash
- Dash for display
- Bootstrap
- BS Slate

By default, the Dash theme is selected.

#### Manage Dashboard Templates

1. Click **Create a new template**. The **Create Custom Templates** dialog box is invoked.
2. Enter the template name, and then select the widgets for your dashboard.
3. Click **Create**. The dashboard template is created.

To edit the template:

1. Click the edit icon beside the template name. The **Edit Template Details** screen is invoked.
2. Check/uncheck the widget options to add/delete widgets from the dashboard.
3. Click **Save**.

To delete a template:

- Click the Delete icon beside the template name. System prompts a message to confirm or cancel deletion. Click **Delete** to confirm deletion.
  The template is deleted.

**Note:** You cannot delete templates that are being used in existing dashboards.

To view a template:

- Click on the template name to view a list of all the widgets in the template.

#### General Configurations for Property Management

In this tab, you can manage the application properties for all the collectors in Hygieia by specifying the server URL and account details for each application. In the Admin screen, the **General Configurations** tab displays a list of all the collectors in Hygieia. To manage the application properties for a specific application, such as GitHub, enter the following details:

- **Url** - Enter the server URL along with the port from where the jobs/applications have to be fetched.
 
- **Username** - Enter the functional username which has access to all jobs/applications.

- **Password** - Enter the password corresponding to the functional username.

Click the **Add** icon to include details of additional server instances for an application. Click **Delete** icon to remove server instances for an application.

Once you finish entering the application details, click **Save**.
