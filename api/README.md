
## Hygieia API

This contains all common REST api services that work with source data system data, which has already
been collected by other service tasks.  This is an abstraction from the local data layer, and the source
system data layer.  All REST controllers should be generic to their purpose, and should not be specific
to any given source system.

To run this in development type the following:

    mvn jetty:run-war
    
It should then be running on http://localhost:8080/api/    