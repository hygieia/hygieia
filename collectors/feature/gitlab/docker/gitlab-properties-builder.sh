#!/bin/bash

if [ "$SKIP_PROPERTIES_BUILDER" = true ]; then
  echo "Skipping properties builder"
  exit 0
fi

# mongo container provides the HOST/PORT
# api container provided DB Name, ID & PWD

if [ "$TEST_SCRIPT" != "" ]
then
        #for testing locally
        PROP_FILE=application.properties
else 
	PROP_FILE=hygieia-gitlab-feature-collector.properties
fi
  
if [ "$MONGO_PORT" != "" ]; then
	# Sample: MONGO_PORT=tcp://172.17.0.20:27017
	MONGODB_HOST=`echo $MONGO_PORT|sed 's;.*://\([^:]*\):\(.*\);\1;'`
	MONGODB_PORT=`echo $MONGO_PORT|sed 's;.*://\([^:]*\):\(.*\);\2;'`
else
	env
	echo "ERROR: MONGO_PORT not defined"
	exit 1
fi

echo "MONGODB_HOST: $MONGODB_HOST"
echo "MONGODB_PORT: $MONGODB_PORT"


cat > $PROP_FILE <<EOF
#Database Name
dbname=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_DATABASE:-dashboard}

#Database HostName - default is localhost
dbhost=${MONGODB_HOST:-10.0.1.1}

#Database Port - default is 27017
dbport=${MONGODB_PORT:-27017}

#Database Username - default is blank
dbusername=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_USERNAME:-db}

#Database Password - default is blank
dbpassword=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_PASSWORD:-dbpass}

#Collector schedule (required)
gitlab.cron=${GITLAB_CRON:-0 0/5 * * * *}

#Gitlab host (optional, defaults to "gitlab.com")
gitlab.host=${GITLAB_HOST:-}

#Gitlab protocol (optional, defaults to "http")
gitlab.protocol=${GITLAB_PROTOCOL:-}

#Gitlab port (optional, defaults to protocol default port)
gitlab.port=${GITLAB_PORT:-}

#Gitlab path (optional, defaults to no path)
gitlab.path=${GITLAB_PATH:-}
  
#Gitlab API Token (required, must be an admin account to retrieve all teams for the instance of gitlab.  If not admin, will only retrieve teams the user belongs to)
gitlab.apiToken=${GITLAB_API_TOKEN:-}

#Gitlab selfSignedCertificate (optional, defaults to false, set to true if your instance of gitlab is running on https without a trusted certificate
gitlab.selfSignedCertificate=${GITLAB_SELF_SIGNED_CERTIFICATE:-false}

#Gitlab API Version (optional, defaults to current version of 4)
gitlab.apiVersion=${GITLAB_API_VERSION:-4}

EOF

echo "

===========================================
Properties file created `date`:  $PROP_FILE
Note: passwords hidden
===========================================
`cat $PROP_FILE |egrep -vi password`
 "

exit 0
