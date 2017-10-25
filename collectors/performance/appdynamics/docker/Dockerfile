FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

RUN \
  mkdir /hygieia

COPY hygieia/ /hygieia
COPY appdynamics-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./appdynamics-properties-builder.sh &&\
   java -Djava.security.egd=file:/dev/./urandom -jar appdynamics-collector.jar --spring.config.location=/hygieia/appdynamics.properties
