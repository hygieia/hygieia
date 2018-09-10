
FROM docker.io/openjdk:8-jre-alpine

MAINTAINER Hygieia@capitalone.com

RUN mkdir /hygieia /hygieia/config

COPY *.jar /hygieia/
COPY properties-builder.sh /hygieia/
COPY logback.xml /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

ENV PROP_FILE /hygieia/config/application.properties
ENV CACERTS /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/cacerts

CMD ./properties-builder.sh && \
  java -Dlogback.configurationFile=/hygieia/logback.xml -jar bitbucket-scm-collector*.jar --spring.config.location=$PROP_FILE

