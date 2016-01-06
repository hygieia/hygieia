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

if [ ! -d logs ] 
then
	mkdir logs
fi
LOG=logs/$PROP_FILE.log


if [ "$MONGO_PORT" != "" ]; then
  # Sample: MONGO_PORT=tcp://172.17.0.20:27017
  MONGODB_HOST=`echo $MONGO_PORT|sed 's;.*://\([^:]*\):\(.*\);\1;'`
  MONGODB_PORT=`echo $MONGO_PORT|sed 's;.*://\([^:]*\):\(.*\);\2;'`
fi


cat > $PROP_FILE <<EOF
#Database Name
database=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_DATABASE:-dashboard}

#Database HostName - default is localhost
dbhost=${MONGODB_HOST:-10.0.1.1}

#Database Port - default is 27017
dbport=${MONGODB_PORT:-9999}

#Database Username - default is blank
dbusername=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_USERNAME:-db}

#Database Password - default is blank
dbpassword=${HYGIEIA_API_ENV_SPRING_DATA_MONGODB_PASSWORD:-dbpass}

#Collector schedule (required)
feature.cron=${JIRA_CRON:-"0 * * * * *"}

#Page size for data calls (Jira maxes at 1000)
feature.pageSize=${JIRA_PAGE_SIZE:-1000}

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
feature.jiraProxyUrl=${JIRA_PROXY_URL}
feature.jiraProxyPort=${JIRA_PROXY_PORT}

# Trending Query:  Number of days in a sprint (not-required)
feature.sprintDays=${JIRA_SPRINT_DAYS:-60}

# Trending Query:  Length of sprint week (not-required)
feature.sprintEndPrior=${JIRA_SPRINT_END_PRIOR:-7}

#Scheduled Job prior minutes to recover data created during execution time (usually, 2 minutes is enough)
feature.scheduledPriorMin=${JIRA_SCHEDULED_PRIOR_MIN:-2}

#Delta change date that modulates the collector item task - should be about as far back as possible, in ISO format (required)
feature.deltaCollectorItemStartDate=${JIRA_DELTA_COLLECTOR_ITEM_START_DATE:-2008-01-01T00:00:00.000000}

#Jira Connection Details
feature.jiraBaseUrl=${JIRA_BASE_URL:-https://jira.atlassian.com}
feature.jiraQueryEndpoint=${JIRA_QUERY_ENDPOINT:-rest/api/2/}

#64-bit encoded credentials with the pattern username:password 
#on a mac you con create them with : echo "username:password" | base64
#reference:  https://www.base64decode.org/ 
feature.jiraCredentials=${JIRA_CREDENTIALS}

#OAuth2.0 token credentials (currently not supported in this version)
feature.jiraOauthAuthtoken=${JIRA_OAUTH_AUTH_TOKEN:-sdfghjkl==}
feature.jiraOauthRefreshtoken=${JIRA_OAUTH_REFRESH_TOKEN:-sdfagheh==}
feature.jiraOauthRedirecturi=${JIRA_OAUTH_REDIRECT_URL:-uri.this.is.test:uri}
feature.jiraOauthExpiretime=${JIRA_OAUTH_EXPIRE_TIME:-234567890987}

#Start dates from which to begin collector data, if no other data is present - usually, a month back is appropriate (required)
feature.deltaStartDate=${JIRA_DELTA_START_DATE:-2015-03-01T00:00:00.000000}
feature.masterStartDate=${JIRA_MASTER_START_DATE:-2008-01-01T00:00:00.000000}

EOF

echo "

===========================================
Properties file created:  $PROP_FILE
===========================================

 " >>$LOG
