
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

ARG JAR_FILE

RUN mkdir /hygieia /hygieia/config

COPY target/${JAR_FILE} /hygieia/artifactory-artifact-collector.jar
COPY artifactory-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./artifactory-properties-builder.sh && \
  java -jar artifactory-artifact-collector.jar --spring.config.location=/hygieia/config/hygieia-artifactory-artifact-collector.properties
