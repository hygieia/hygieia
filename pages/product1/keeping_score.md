---
title: Keeping Score for Team Dashboard (Dashboard Gamification)
tags: 
type: 
homepage: 
toc: true
sidebar: product1_sidebar
permalink: keeping_score.html
---

Team Dashboard scores encourage teams to fully configure the CI/CD pipeline by evaluating the widgets based on a set of metrics.

**Note:** Scoring your team dashboard is optional. However, it has the potential to foster healthy competitiveness within your organization.

![Image](http://capitalone.github.io/Hygieia/media/images/Dashboard_Gamification_Overview.png)

By keeping score for the team dashboards, you are able to:

- Achieve DevOps maturity by giving scores based on algorithms
- Challenge the team to increase their scores on the team dashboard
- Keep code repositories clean and encourage practices required for quality assurance

Each widget has a score that is based on good practices for that widget. For example, the Source Code Management widget, also known as the Code Repo widget, will have a 5-star score if you are using Pull Requests (PRs), and are integrating to the master branch at least once per day for each developer on the team.

The Gamification module displays an overall score for your team dashboard by considering the star rating of the following widgets:

| Widget | Scoring Criteria |
|--------|------------------|
| Code Repo | Rated on the number of commits and use of PRs for integrating to the master branch. For example, data is present for more than or equal to 60% days for the last 14 days. In addition, the team is integrating to the master branch at least once per day for each developer in the team. |
| Build | Rated on the status and duration of the build  (for the past 14 days) |
| Quality | Rated on code coverage (less than 20%), unit tests passed, and violation data. Violations is calculated as: ```100 - ((blockers * 20) + (critical * 5) + (major * 1))```. Here, **blockers**, **critical**, and **major** indicate the severity level for a violation |
| Deploy | Rated on deployment success and the number of deploy instances that are online |

Each widget is given a star rating on a scale from 0 to 5, with 5 stars being the highest score for each widget. Hygieia in turn calculates the overall score of the team dashboard, which is an aggregate of the individual widget ratings. The widgets are given equal weight while rating the team dashboard. The score of the widgets is combined and aggregated to determine the overall score for the team dashboard. 

## Enable Scores for a New Dashboard

To enable scoring for a new dashboard, check **Enable Score** in the **Create a New Dashboard** window.

![Image](http://capitalone.github.io/Hygieia/media/images/DashboardGamification_NewDashboard.png)

For details on other fields in this screen, see the [Create a Team Dashboard](select_dashboard.md#create-a-team-dashboard) section.

**Score Tab**

On the **Administer Your Dashboard** screen, click on the **Score** tab for the settings related to the scoring feature:

**Enable Score** – Check this box to indicate that you want to enable the scoring feature for an existing team dashboard.

For an existing dashboard, uncheck **Enable Score** to remove the star ratings.

Select one of the following radio-buttons to choose how the score appears on the dashboard:

- **Display in Header** – Select this option to display the overall dashboard score at the top of the team dashboard. Click on the star rating to view the score details.

  If you select this option, to view score details, click on the score. The Score Details window pops up.
  
- **Display in Widget** – Choose this option to display the overall score as a widget on the dashboard. Click **View Details** on the widget to see the score details.

## Enable or Disable Scores for an Existing Dashboard

The owner or administrator of the dashboard can either enable or disable dashboard scores for an existing dashboard. You can do this from the **Administer Your Dashboard** screen. 

![Image](http://capitalone.github.io/Hygieia/media/images/DashboardGamification_Administer.png)

Click the Settings icon to invoke the **Administer Your Dashboard** screen. In this screen, click on the **Score** tab for settings related to the scoring feature:

**Enable Score** – Check this box to indicate that you want to enable the scoring feature for an existing team dashboard. 

For an existing dashboard, uncheck **Enable Score** to remove the star ratings.

Select one of the following radio-buttons to choose how the score appears on the dashboard:

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