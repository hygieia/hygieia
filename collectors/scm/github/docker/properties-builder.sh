#!/bin/bash
cat > $PROP_FILE <<EOF
#Database Name
dbname=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_DATABASE:-dashboarddb}

#Database HostName - default is localhost
dbhost=${MONGODB_HOST:-10.0.1.1}

#Database Port - default is 27017
dbport=${MONGODB_PORT:-27017}

#Database Username - default is blank
dbusername=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_USERNAME:-dashboarduser}

#Database Password - default is blank
dbpassword=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_PASSWORD:-dbpassword}

#Collector schedule (required)
github.cron=${GITHUB_CRON:-0 0/5 * * * *}

github.host=${GITHUB_HOST:-github.com}

#Maximum number of days to go back in time when fetching commits
github.commitThresholdDays=${GITHUB_COMMIT_THRESHOLD_DAYS:-15}

#Optional: Error threshold count after which collector stops collecting for a collector item. Default is 2.
github.errorThreshold=${GITHUB_ERROR_THRESHOLD:-1}

#This is the key generated using the Encryption class in core
github.key=${GITHUB_KEY}

#personal access token generated from github and used for making authentiated calls
github.personalAccessToken=${PERSONAL_ACCESS_TOKEN}

EOF

echo "
===========================================
Properties file created `date`:  $PROP_FILE
Note: passwords hidden
===========================================
`cat $PROP_FILE | egrep -vi password`
===========================================
END PROPERTIES FILE
===========================================
"

exit 0
