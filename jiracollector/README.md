#Jira Feature Collector
Retrieves feature content data from the source system APIs and places it in a MongoDB for later retrieval and use by the DevOps Dashboard

##Introduction
The collector component aims to provide following capabilities
<ul>
<li> Enable periodic data upload from the DevOps tools into database</l1>
<li> No duplicate data is entered
</ul>

Prepping your Eclipse IDE for development
-----------------------------------------
Make sure below eclipse plugins are installed on your eclipse instance
<ul>
<li>Eclipse Egit Team Provider plugin</li>
</ul>


Getting Started
===============
To get you started you can simply clone the project on your workstation and import it as an existing project
in your eclipse IDE; ensure that the name of the general project is the same as the GitHub repository name.

Install Dependencies
-------------------------------------------
Once the project is imported in you can right click on your project and choose option
<ul>
<li>Configure->convert to Maven Project</li>
</ul>

Run as Singleton Application
-------------------------------------------
In order to run this application, build with Maven and run the JAR labeled with the suffix of "...-jar-with-dependencies.jar".  Once run, the Spring Service will iterate through its scheduled services.  Be sure to copy the jira-feature-collector.properties file(s) to your respective
OS' temp directory (See below).  Also, when running in production, be sure to change the active spring profile in one of the following properties files, depending on your OS, to say "prod":
<ul>
<li><strong>Windows 7:</strong>&nbsp;\temp\application.properties</li>
<li><strong>Mac OSX:</strong>&nbsp;/Users/Shared/properties/application.properties</li>
<li><strong>Linux/Unix:</strong>&nbsp;/prod/msp/app/dashboard_apps/application.properties</li>
</ul>
You must also run the jar command with the following command line argument:  <strong>-Dspring.config.name=jira-feature-collector</strong>
<em>
