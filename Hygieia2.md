### How to use Hygieia 2.0 features

Following are the key features delivered with Hygieia 2.0

- A Product or Program level view
- Displaying flow of commits from commit to production stages on the new view
- Allowing Github webhook to push data into Hygieia
- A Jenkins plugin to publish data into Hygieia


#### What is a Product Level View
Hygieia 1.0 was about creating a dashboard for a team with 1 story management source, 1 code repository, 1 build job etc. In many cases things are more complex than that. 
A single team may have multiple of these or a large product team will definitely have multiple of these. 

It is very important to have a "rollup" view for the entire product and visualize product health and speed at the same time.

Hygieia 2.0 attempts to achieve this by "rolling up" data from individual Team Dashboard and apply statistical analysis to trend on health and speed.

#### Prerequisites to use Hygieia 2.0 feature
- First is to install the latest UI, API, Collectors.
 
- Install Jenkins Plugin on your Jenkins build machine. It is quite possible to create plugins for other CI tools such as Go.cd or Travis etc. See [Jenkins Plugin page] (/hygieia-jenkins-plugin) for details on installation and configuration.
Jenkins plugin allows Hygieia to create link between code commit, build, artifact, deployments etc. 

- Optionally, if you are using Github Enterprise, you can set up Webhook (see screenshot below):
![Image](/media/images/webhook.png)

- Now, setup Team Dashboards as you would with Hygieia 1.0. For each Team Dashboard, create your pipeline:
![Image](/media/images/team-pipeline-config.png)


#### Hygieia 2.0, Screen Flows

##### Select Dashboard:
![Image](/media/images/h2-select-dashboard.png)

##### Create New Dashboard:
![Image](/media/images/h2-create-dashboard.png)

##### Add Team Dashboard(s) to New Product Dashboard:
![Image](/media/images/h2-add-teamdashboard.png)




#### Hygieia 2.0 Dashboard - Each part explained

##### Commit Stage:
![Image](/media/images/h2-commit-stage.png)

##### Build Stage:
![Image](/media/images/h2-build-stage.png)

##### Deployment Stages:
![Image](/media/images/h2-deploy-stages.png)

##### Production Stage:
![Image](/media/images/h2-prod-stage.png)

##### Commit Details at every stage:
![Image](/media/images/h2-commit-details-stage.png)

##### Pipeline Health:
![Image](/media/images/h2-health.png)

##### Pipeline Health Details:
![Image](/media/images/h2-health-details.png)
