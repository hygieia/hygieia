[![Build Status](https://jenkinskdc.kdc.capitalone.com/itojenkins/buildStatus/icon?job=Dashboard-API-Jay)](https://jenkinskdc.kdc.capitalone.com/itojenkins/view/Dashboard/job/Dashboard-API-Jay/)

api
===
This contains all common REST api services that work with source data system data, which has already
been collected by other service tasks.  This is an abstraction from the local data layer, and the source
system data layer.  All REST controllers should be generic to their purpose, and should not be specific
to any given source system.
