# Hygieia API

This contains all common REST api services that work with source data system data, which has already
been collected by other service tasks.  This is an abstraction from the local data layer, and the source
system data layer.  All REST controllers should be generic to their purpose, and should not be specific
to any given source system.


# API Properties file

The API layer needs a property file in following format:

```properties
# dashboard.properties
dbname=dashboarddb
dbusername=[MogoDb Database Username]
dbpassword=[MongoDb Database Password]
dbhost=[Host on which MongoDb is running]
dbport=[Port on which MongoDb is listening]
```

For the API web application to use this property file, make sure that the following environment variable is set in the following format:

```bash
DASHBOARD_PROP=[path to dashboard.properties file]
```


# Run the API
 ### 1. Embedded Jetty container

```bash
mvn jetty:run   -DPROP_FILE=<Path to dashboard.properties file>
```

  ### 2. Tomcat container
   * Copy the genrated war file  from project target folder after you have build the project into tomcat webapps folder
   * Add a file setEnv.sh into Tomcat bin folder with following content
   ```bash
   export DASHBOARD_PROP=<Path to dashboard.properties file>
   ```
   * Start the tomcat container.
