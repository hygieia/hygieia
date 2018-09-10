
FROM docker.io/openjdk:8-jdk-alpine

MAINTAINER Hygieia@capitalone.com

RUN apk add --no-cache bash

RUN mkdir /hygieia /hygieia/config

COPY hygieia /hygieia
COPY properties-builder.sh /hygieia/
COPY logback.xml /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

ENV PROP_FILE /hygieia/config/hygieia-api-audit.properties

EXPOSE 8080
CMD ./properties-builder.sh &&\
  java -Djava.security.egd=file:/dev/./urandom -Dlogback.configurationFile=/hygieia/logback.xml -jar *.jar --spring.config.location=$PROP_FILE
