#!/bin/bash

# mongo container provides the HOST/PORT
# api container provided DB Name, ID & PWD
if [ "$TEST_SCRIPT" != "" ]
then
	#for testing locally
	PROP_FILE=application.properties
else
	PROP_FILE=hygieia-jira-feature-collector.properties
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
database=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_DATABASE:-dashboard}

#Database HostName - default is localhost
dbhost=${MONGODB_HOST:-10.0.1.1}

#Database Port - default is 27017
dbport=${MONGODB_PORT:-27017}


#Database Username - default is blank
dbusername=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_USERNAME:-db}

#Database Password - default is blank
dbpassword=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_PASSWORD:-dbpass}

#Collector schedule (required)
feature.cron=0 * * * * *

#Page size for data calls (Jira maxes at 1000)
feature.pageSize=1000

#In-built folder housing prepared REST queries (required)
feature.queryFolder=jiraapi-queries

#Jira API Query file names (String template requires the files to have .st extension) (required)
feature.storyQuery=story
feature.epicQuery=epic
feature.projectQuery=projectinfo
feature.memberQuery=memberinfo
feature.sprintQuery=sprintinfo
feature.teamQuery=teaminfo
feature.trendingQuery=trendinginfo

#Jira Connection Details
feature.jiraProxyUrl=${SPRING_DATA_JIRA_PROXY_URL}
feature.jiraProxyPort=${SPRING_DATA_JIRA_PROXY_PORT}

# Trending Query:  Number of days in a sprint (not-required)
feature.sprintDays=${SPRING_DATA_SPRINT_DAYS:-60}

# Trending Query:  Length of sprint week (not-required)
feature.sprintEndPrior=${SPRING_DATA_SPRINT_END_PRIOR:-7}

#Scheduled Job prior minutes to recover data created during execution time (usually, 2 minutes is enough)
feature.scheduledPriorMin=2

#Delta change date that modulates the collector item task - should be about as far back as possible, in ISO format (required)
feature.deltaCollectorItemStartDate=2008-01-01T00:00:00.000000

#Jira Connection Details
feature.jiraBaseUrl=${SPRING_DATA_JIRA_BASE_URL:-https://jira.atlassian.com}
feature.jiraQueryEndpoint=rest/api/2/
#64-bit encoded credentials with the pattern username:password
feature.jiraCredentials=${SPRING_DATA_JIRA_CREDENTIALS:-YWxsIHlvdXIgYmFzZSBhcmUgYmVsb25nIHRvIHVzOiB5b3UgYXJlIG9uIHRoZSB3YXkgdG8gZGVzdHJ1Y3Rpb246IG1ha2UgeW91ciB0aW1l}
#OAuth2.0 token credentials (currently not supported in this version)
feature.jiraOauthAuthtoken=sdfghjkl==
feature.jiraOauthRefreshtoken=sdfagheh==
feature.jiraOauthRedirecturi=uri.this.is.test:uri
feature.jiraOauthExpiretime=234567890987

#Start dates from which to begin collector data, if no other data is present - usually, a month back is appropriate (required)
feature.deltaStartDate=2015-03-01T00:00:00.000000
feature.masterStartDate=2008-01-01T00:00:00.000000

EOF

echo "
===========================================
Properties file created:  $PROP_FILE
Note: passwords hidden
===========================================
`cat $PROP_FILE |egrep -vi password`
" 
