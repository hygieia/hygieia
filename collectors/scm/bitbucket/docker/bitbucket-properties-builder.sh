#!/bin/bash

# mongo container provides the HOST/PORT
# api container provided DB Name, ID & PWD

if [ "$TEST_SCRIPT" != "" ]
then
        #for testing locally
        PROP_FILE=application.properties
else 
	PROP_FILE=hygieia-bitbucket-scm-collector.properties
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
git.cron=${BITBUCKET_CRON:-0 0/5 * * * *}

#mandatory
git.host=${BITBUCKET_HOST:-mybitbucketrepo.com/}
git.api=${BITBUCKET_API:-/rest/api/1.0/}

#Maximum number of days to go back in time when fetching commits. Only applicable to Bitbucket Cloud.
git.commitThresholdDays=${BITBUCKET_COMMIT_THRESHOLD_DAYS:-15}

#Page size for rest calls. Only applicable to Bitbucket Server.
git.pageSize=${BITBUCKET_PAGE_SIZE,-25}

#Bitbucket product
# Set to "cloud" to use Bitbucket Cloud (formerly known as Bitbucket)
# Set to "server" to use Bitbucket Server (formerly known as Stash)
# More information can be found here: href="https://github.com/capitalone/Hygieia/issues/609
git.product=${BITBUCKET_PRODUCT:-cloud}

EOF

echo "

===========================================
Properties file created `date`:  $PROP_FILE
Note: passwords hidden
===========================================
`cat $PROP_FILE |egrep -vi password`
 "

exit 0
