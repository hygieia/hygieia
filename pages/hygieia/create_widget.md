---
title: Guidelines to Create a Widget
tags: 
summary: These brief instructions will help you create a new widget on the Hygieia dashboard.
toc: true
sidebar: hygieia_sidebar
permalink: create_widget.html
---

To create a new widget in Hygieia:

1. Model your widget after one of the existing widgets, such as the deploy or build widget available at `/UI/src/components/widgets/`. Customize your module, config, style, view, html,  and ".js" files as needed.

2. For the widget to appear on the dashboard, use one of the following options:

   **Option 1**
   
   In the `UI/src/components/templates/` directory, add the following section in the template to match the widget you are building:


	```html
	<div class="container-fluid" widget-container dashboard="ctrl.dashboard" ng-if="template.widgetView == 'your-widget-name'">
			<div class="row">
				<div class="col-xs-12">
					<widget name="your-widget-name" widget-title="Your-widget-title"></widget>
				</div>
			</div>
		</div>
	```
	
	**Option 2**
	
	When you create a team dashboard in Hygieia, in the **Create a new dashboard** screen, choose **Select Widgets** radio button. Enter all other required information and click **Create**. This invokes the **Widget Management** screen. From the list of widgets, select your widget along with any other widgets, and then click **Create**.
	
	![Image](http://capitalone.github.io/Hygieia/media/images/widget_management.png)
	
	For more information, see the [Team Dashboard](../product1/create_team_dashboard.md) documentation. 
	
	