
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

ARG JAR_FILE

RUN mkdir /hygieia /hygieia/config

COPY target/${JAR_FILE} /hygieia/xldeploy-deployment-collector.jar
COPY xldeploy-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./xldeploy-properties-builder.sh && \
  java -jar xldeploy-deployment-collector.jar --spring.config.location=/hygieia/config/hygieia-xldeploy-deployment-collector.properties
