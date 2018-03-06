
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

ARG JAR_FILE

RUN mkdir /hygieia /hygieia/config

COPY target/${JAR_FILE} /hygieia/chat-ops-collector.jar
COPY chat-ops-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./chat-ops-properties-builder.sh && \
  java -jar chat-ops-collector.jar --spring.config.location=/hygieia/config/hygieia-chat-ops-collector.properties
