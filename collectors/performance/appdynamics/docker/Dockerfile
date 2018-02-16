FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

RUN mkdir /hygieia /hygieia/config

COPY hygieia/ /hygieia
COPY properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

ENV PROP_FILE /hygieia/config/application.properties

EXPOSE 8080
CMD /bin/sh ./properties-builder.sh &&\
  java -Djava.security.egd=file:/dev/./urandom -jar appdynamics-collector.jar --spring.config.location=$PROP_FILE
