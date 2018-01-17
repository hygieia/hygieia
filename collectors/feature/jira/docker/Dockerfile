
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

RUN mkdir /hygieia /hygieia/config

COPY *.jar /hygieia/
COPY jira-properties-builder.sh /hygieia/

WORKDIR /hygieia/

VOLUME ["/hygieia/logs"]

CMD ./jira-properties-builder.sh &&\
	 java -jar jira-feature-collector*.jar --spring.config.location=config/hygieia-jira-feature-collector.properties
