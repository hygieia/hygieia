
Hygieia API
===
This contains all common REST api services that work with source data system data, which has already
been collected by other service tasks.  This is an abstraction from the local data layer, and the source
system data layer.  All REST controllers should be generic to their purpose, and should not be specific
to any given source system.


###API Properties file

Api layer needs a property file in following format

```
dbname=[dashboarddb]  
dbusername=[MogoDb Database Username]  
dbpassword=[MongoDb Database Password]  
dbhost=[Host on which MongoDb is running]  
dbport=[Port on which MongoDb is listening]  
```

For API web application to use this property file make sure that following environment property is set in following format

DASHBOARD_PROP=[path to dashboard.properties file]  
