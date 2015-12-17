
FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir  /hygieia

COPY *.jar /hygieia/
COPY properties-builder.sh /hygieia/jira-feature-properties-builder.sh

WORKDIR /hygieia/

VOLUME ["/hygieia/logs"]

CMD ./jira-feature-properties-builder.sh && java -jar jira-feature-collector*.jar --spring.config.location=hygieia-jira-feature-collector.properties
