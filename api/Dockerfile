FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

ARG JAR_FILE

RUN mkdir /hygieia /hygieia/config

COPY target/${JAR_FILE} /hygieia/api.jar
COPY api-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

EXPOSE 8080
CMD ./api-properties-builder.sh &&\
  java -Djava.security.egd=file:/dev/./urandom -jar api.jar --spring.config.location=/hygieia/config/hygieia-api.properties