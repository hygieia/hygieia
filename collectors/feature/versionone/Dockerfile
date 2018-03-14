
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

ARG JAR_FILE

RUN mkdir /hygieia /hygieia/config

COPY target/${JAR_FILE} /hygieia/
COPY versionone-properties-builder.sh /hygieia/versionone-feature-collector.jar

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./versionone-properties-builder.sh && \
  java -jar versionone-feature-collector.jar --spring.config.location=/hygieia/config/hygieia-versionone-feature-collector.properties
