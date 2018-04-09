---
title: Keeping Score for Team Dashboard
tags: 
type: 
homepage: 
toc: true
sidebar: product1_sidebar
permalink: keeping_score.html
---

Team Dashboard scores encourage teams to fully configure the CI/CD pipeline by evaluating the widgets based on a set of metrics.

![Image](http://capitalone.github.io/Hygieia/media/images/Dashboard_Gamification_Overview.png)

Hygieia displays an overall score for your team dashboard by considering the star rating of the following widgets:

- Code Repo - rated on the code coverage
- Build - rated on the build success
- Quality - rated on the unit test success
- Deploy - rated on deployment success and the number of deploy instances that are online

Each of these widgets is given a star rating from 0 to 5. Hygieia in turn calculates the overall score of your team dashboard, which is an aggregate of the individual widget ratings. The widgets are given equal weight while rating the team dashboard.

By keeping score for the team dashboards, Hygieia allows you to:

- Achieve DevOps maturity goals
- Move towards pipeline resiliency
- Determine if your dashboard adheres to policies and standards

## Create a New Dashboard with Scores Enabled

To enable scoring for a new dashboard, check **Enable Score** in the **Create a New Dashboard** window. For more details, see the [Create a Team Dashboard](select_dashboard.md#create-a-team-dashboard) section.

## Enable or Disable Scores for an Existing Dashboard

The owner or administrator of the dashboard can either enable or disable dashboard scores for an existing dashboard. You can do this from the **Administer Your Dashboard** screen. 

Click the Settings icon to invoke the **Administer Your Dashboard** screen. In this screen, click on the **Score** tab for settings related to the scoring feature:

**Enable Score** – Check this box to indicate that you want to enable the scoring feature for an existing team dashboard. 

For an existing dashboard, uncheck **Enable Score** to remove the star ratings.

Select one of the following radio-buttons to choose how the score should appear on the dashboard:

- **Display in Header** – Select this option to display the overall dashboard score at the top of the team dashboard. Click on the star rating to view the score details.

  If you select this option, to view score details, click on the score. The Score Details window pops up.
  
- **Display in Widget** – Choose this option to display the overall score as a widget on the dashboard. Click **View Details** on the widget to see the score details.  

The **Score Details** pop-up offers a breakdown on the ratings for each of the widgets configured on the team dashboard. These details include:

- The list of widgets
- Processing status of the widget (either processed or failed)
- A message describing a status
- Weight assigned to each widget
- Star rating for each widget

![Image](http://capitalone.github.io/Hygieia/media/images/Dashboard_Gamification_ScoreDetails.png)

For additional information on **Administer Your Dashboard** screen, see [Change Dashboard Settings](dashboard_administration.md#change-dashboard-settings) section.