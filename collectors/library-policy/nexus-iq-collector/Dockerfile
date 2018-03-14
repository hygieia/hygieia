
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

ARG JAR_FILE

RUN mkdir /hygieia /hygieia/config

COPY target/${JAR_FILE} /hygieia/
COPY nexus-iq-properties-builder.sh /hygieia/nexus-iq-collector.jar

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./nexus-iq-properties-builder.sh && \
  java -jar nexus-iq-collector.jar --spring.config.location=/hygieia/config/hygieia-nexus-iq-collector.properties
