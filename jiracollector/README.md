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
Please follow the instructions to point your Eclipse IDE to Capitalone Eclipse Update site for installing required plugins
https://pulse.kdc.capitalone.com/docs/DOC-56399
<ul>
<li>Eclipse Egit Team Provider plugin</li>
</ul>

Tomcat Server for Testing your application
--------------------------------------------
Download the Tomcat server for the platform you are developing  from internal Nexus here:  <a href="https://nexus.kdc.capitalone.com/mother/content/repositories/thirdparty/org/apache/tomcat/apache-tomcat/7.0.54/apache-tomcat-7.0.54-x64.zip">Tomcat Server</a><br/>
you can follow the instructions <a href="http://theopentutorials.com/tutorials/java-ee/how-to-configure-apache-tomcat-in-eclipse-ide/">Here</a>

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
<li><strong>Windows 7:</strong>&nbsp;/temp/application.properties</li>
<li><strong>Mac OSX:</strong>&nbsp;/Users/Shared/properties/application.properties</li>
<li><strong>Linux/Unix:</strong>&nbsp;/prod/msp/app/dashboard_apps/application.properties</li>
</ul>
You must also run the jar command with the following command line argument:  <strong>-Dspring.config.name=jira-feature-collector</strong>
<em>Note:</em>Jira requires that certain properties are made available for the live data connection to the
source system.  Please ensure to provide the property content found at the following repository readme:
https://github.kdc.capitalone.com/VersionOne/jira-client
