
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

ARG JAR_FILE

RUN mkdir /hygieia /hygieia/config

COPY target/${JAR_FILE} /hygieia/
COPY udeploy-properties-builder.sh /hygieia/udeploy-deployment-collector.jar

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./udeploy-properties-builder.sh && \
  java -jar udeploy-deployment-collector.jar --spring.config.location=/hygieia/config/hygieia-udeploy-deployment-collector.properties
