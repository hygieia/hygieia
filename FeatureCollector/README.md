#Version One Feature Collector
Retrieves VersionOne feature content data from the source system APIs and places it in a MongoDB for later retrieval and use by the DevOps Dashboard

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
Download the Tomcat server for the platform you are developing from Nexus online (version 7 or higher)
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
In order to run this application, build with Maven and run the JAR labeled "versionone-feature-collector.jar".  Once run, the Spring Service will iterate through its scheduled services.  Be sure to create an versionone-feature-collector.properties file in the
same directory as your runnable JAR file.