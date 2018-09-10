
FROM docker.io/openjdk:8-jre-alpine

MAINTAINER Hygieia@capitalone.com

RUN apk add --no-cache bash

RUN \
  mkdir /hygieia /hygieia/config

COPY *.jar /hygieia/
COPY properties-builder.sh /hygieia/
COPY logback.xml /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

ENV PROP_FILE /hygieia/config/application.properties

CMD ./properties-builder.sh && \
  java -Dlogback.configurationFile=/hygieia/logback.xml -jar github-graphql-scm-collector*.jar --spring.config.location=$PROP_FILE

