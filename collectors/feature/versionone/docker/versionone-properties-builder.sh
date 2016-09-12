#!/bin/bash

# mongo container provides the HOST/PORT
# api container provided DB Name, ID & PWD

if [ "$TEST_SCRIPT" != "" ]
then
        #for testing locally
        PROP_FILE=application.properties
else 
	PROP_FILE=hygieia-versionone-feature-collector.properties
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

#Page size for data calls (VersionOne recommended 2000)
feature.pageSize=${VERSIONONE_PAGE_SIZE:-2000}

#In-built folder housing prepared REST queries (required)
feature.queryFolder=${VERSIONONE_QUERY_FOLDER:-v1api-queries}

#Jira API Query file names (String template requires the files to have .st extension) (required)
feature.storyQuery=${VERSIONONE_STORY_QUERY:-story}
feature.epicQuery=${VERSIONONE_EPIC_QUERY:-epicinfo}
feature.projectQuery=${VERSIONONE_PROJECT_QUERY:-projectinfo}
feature.memberQuery=${VERSIONONE_MEMBBER_QUERY:-memberinfo}
feature.sprintQuery=${VERSIONONE_SPRINT_QUERY:-sprintinfo}
feature.teamQuery=${VERSIONONE_TEAM_QUERY:-teaminfo}
feature.trendingQuery${{VERSIONONE_TRENDING_QUERY:-trendinginfo}

# Trending Query:  Number of days in a sprint (not-required)
feature.sprintDays=${VERSIONONE_SPRINT_DAYS:-60}
# Trending Query:  Length of sprint week (not-required)
feature.sprintEndPrior=${VERSIONONE_SPRINT_END_PRIOR:-7}

#Scheduled Job prior minutes to recover data created during execution time (usually, 2 minutes is enough)
feature.scheduledPriorMin=${VERSIONONE_SCHEDULED_PRIOR_MIN:-2}

#Delta change date that modulates the collector item task - should be about as far back as possible, in ISO format (required)
feature.deltaCollectorItemStartDate=${VERSIONONE_DELTA_COLLECTORITEM_START_DATE:-2008-01-01T00:00:00.000000}

#VersionOne Connection Details
#Proxy assumes a host:port syntax
feature.versionOneProxyUrl=${VERSIONONE_PROXY_URL:-""}
feature.versionOneBaseUri=${VERSIONONE_URL:-https://www.versionone.com/our-company-instance/}
#Access token provided by VersionOne
feature.versionOneAccessToken=${VERSIONONE_ACCESS_TOKEN:-accessToken}

#Start dates from which to begin collector data, if no other data is present - usually, a month back is appropriate (required)
feature.deltaStartDate=${VERSIONONE_DELTA_START_DATE:-2015-03-01T00:00:00.000000}
feature.masterStartDate=${VERSIONONE_MASTER_START_DATE:-2008-01-01T00:00:00.000000}

EOF

echo "

===========================================
Properties file created `date`:  $PROP_FILE
Note: passwords hidden
===========================================
`cat $PROP_FILE |egrep -vi password`
 "

exit 0
