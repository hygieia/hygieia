FROM docker.io/openjdk:8-jre-alpine

MAINTAINER Hygieia@capitalone.com

RUN mkdir /hygieia /hygieia/config

COPY hygieia/ /hygieia
COPY properties-builder.sh /hygieia/
COPY logback.xml /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

ENV PROP_FILE /hygieia/config/application.properties

EXPOSE 8080
CMD /bin/sh ./properties-builder.sh &&\
  java -Dlogback.configurationFile=/hygieia/logback.xml -Djava.security.egd=file:/dev/./urandom -jar appdynamics-collector.jar --spring.config.location=$PROP_FILE
