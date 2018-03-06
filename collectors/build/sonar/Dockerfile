
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

ARG JAR_FILE

RUN mkdir /hygieia /hygieia/config

COPY target/${JAR_FILE} /hygieia/sonar-codequality-collector.jar
COPY sonar-codequality-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./sonar-codequality-properties-builder.sh && \
  java -jar sonar-codequality-collector.jar --spring.config.location=/hygieia/config/hygieia-sonar-codequality-collector.properties
