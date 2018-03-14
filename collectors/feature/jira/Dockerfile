
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

ARG JAR_FILE

RUN mkdir /hygieia /hygieia/config

COPY target/${JAR_FILE} /hygieia/
COPY jira-properties-builder.sh /hygieia/jira-feature-collector.jar

WORKDIR /hygieia/

VOLUME ["/hygieia/logs"]

CMD ./jira-properties-builder.sh &&\
	 java -jar jira-feature-collector.jar --spring.config.location=config/hygieia-jira-feature-collector.properties
