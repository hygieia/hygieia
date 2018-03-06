
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

ARG JAR_FILE

RUN mkdir /hygieia /hygieia/config

COPY target/${JAR_FILE} /hygieia/gitlab-feature-collector.jar
COPY gitlab-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./gitlab-properties-builder.sh && \
  java -jar gitlab-feature-collector.jar --spring.config.location=/hygieia/config/hygieia-gitlab-feature-collector.properties
