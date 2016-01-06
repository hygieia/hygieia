
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com



# REQUIRED: Jira Connection Details(required)
ENV JIRA_BASE_URL=

# REQUIRED: 64-bit encoded credentials with the pattern username:password
# on a mac you con create them with : echo "username:password" | base64
# reference:  https://www.base64decode.org/
ENV JIRA_CREDENTIALS="username:password"

# Start dates from which to begin collector data
#  if no other data is present 
#  usually a month back is appropriate (required)
ENV JIRA_DELTA_START_DATE=2015-03-01T00:00:00.000000
ENV JIRA_MASTER_START_DATE=2008-01-01T00:00:00.000000

#OPTIONAL ITEMS


# Usually the defaults are OK for these, but you may want to tweek them.  
# See the file jira-feature-collector/docker/jira-properties-builder.sh for default values
# Placed here to make visible to DOCKER env command
ENV JIRA_CRON=
ENV JIRA_PAGE_SIZE=
ENV JIRA_QUERY_ENDPOINT=
ENV JIRA_PROXY_URL=
ENV JIRA_PROXY_PORT=
ENV JIRA_SPRINT_DAYS=
ENV JIRA_SPRINT_END_PRIOR=
ENV JIRA_SCHEDULED_PRIOR_MIN=
ENV JIRA_DELTA_COLLECTOR_ITEM_START_DATE=
ENV JIRA_OAUTH_AUTH_TOKEN=
ENV JIRA_OAUTH_REFRESH_TOKEN=
ENV JIRA_OAUTH_REDIRECT_URL=
ENV JIRA_OAUTH_EXPIRE_TIME=
################################

RUN \

  mkdir  /hygieia

COPY *.jar /hygieia/
COPY jira-properties-builder.sh /hygieia/

WORKDIR /hygieia/

VOLUME ["/hygieia/logs"]

CMD ./jira-properties-builder.sh && java -jar jira-feature-collector*.jar --spring.config.location=hygieia-jira-feature-collector.properties
