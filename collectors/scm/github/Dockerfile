
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

ARG JAR_FILE

RUN mkdir /hygieia /hygieia/config

COPY target/${JAR_FILE} /hygieia/
COPY github-properties-builder.sh /hygieia/github-scm-collector.jar

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./github-properties-builder.sh && \
  java -jar github-scm-collector.jar --spring.config.location=/hygieia/config/hygieia-github-scm-collector.properties
