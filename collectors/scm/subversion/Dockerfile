
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

ARG JAR_FILE

RUN mkdir /hygieia /hygieia/config

COPY target/${JAR_FILE} /hygieia/subversion-collector.jar
COPY subversion-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./subversion-properties-builder.sh && \
  java -jar subversion-collector.jar --spring.config.location=/hygieia/config/hygieia-subversion-collector.properties
