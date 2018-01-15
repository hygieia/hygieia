
FROM docker.io/openjdk:8-jre

MAINTAINER Hygieia@capitalone.com

RUN mkdir /hygieia /hygieia/config

COPY *.jar /hygieia/
COPY bitbucket-properties-builder.sh /hygieia/

WORKDIR /hygieia

VOLUME ["/hygieia/logs"]

CMD ./bitbucket-properties-builder.sh && \
  java -jar bitbucket-scm-collector*.jar --spring.config.location=/hygieia/config/hygieia-bitbucket-scm-collector.properties

