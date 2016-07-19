FROM docker.io/java:openjdk-8-jdk

MAINTAINER Hygieia@capitalone.com


RUN \
  mkdir /hygieia

COPY target /hygieia
COPY properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

EXPOSE 8080
CMD ./properties-builder.sh &&\
  java -Djava.security.egd=file:/dev/./urandom -jar api.jar --spring.config.location=/hygieia/dashboard.properties
