
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

RUN mkdir /hygieia /hygieia/config

COPY *.jar /hygieia/
COPY properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

ENV PROP_FILE /hygieia/config/application.properties
ENV CACERTS /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/cacerts

CMD ./properties-builder.sh && \
  java -jar bitbucket-scm-collector*.jar --spring.config.location=$PROP_FILE

